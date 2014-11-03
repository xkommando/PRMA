package com.caibowen.prma.logger.logback;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.spi.AppenderAttachable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Iterator;
import java.util.concurrent.*;

import static java.lang.System.err;

/**
 * @author BowenCai
 * @since 20-10-2014.
 */
public class AsyncAppenderWrapper<E> extends UnsynchronizedAppenderBase<E>
                                        implements AppenderAttachable<E> {


    // when stop, flush out remaining events within this time (in ms).
    public static final int DEFUALT_FLUSH_TIME = 2000;
    private int flushTime = DEFUALT_FLUSH_TIME;


    // number of thread to append events.
    public static final int DEFAULT_MAX_THREAD = 128;
    @Inject private int maxThreadNum = DEFAULT_MAX_THREAD;

    @Inject private ThreadPoolExecutor executor;

    @Inject private Appender<E> backupAppender;

    @Override
    public void start() {
        if (appList.size() == 0) {
            addError("No attached appender");
            return;
        }

        if (executor == null || executor.isShutdown()) {


            executor = new ThreadPoolExecutor(0, 256, 60L, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>(), new RejectedExecutionHandler() {

                @Override
                public void rejectedExecution(final Runnable r, ThreadPoolExecutor executor) {
                    class _R implements Runnable {
                        @Override
                        public void run() {
                            r.run();
                        }
                    }
                    if (r instanceof _R)
                        r.run();
                    else
                        executor.execute(new _R());
                }
            });
        }
        super.start();
    }


    @Override
    protected void append(final E eventObject) {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    passOnEvent(eventObject);
                }

            });
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                backupAppender.doAppend(eventObject);
            } catch (Exception e) {
                ex.printStackTrace();
            }
        }

    }


    @Override
    public void stop() {
        if (started == false)
            return;
        started = false;
        super.stop();
        BlockingQueue<Runnable> q = executor.getQueue();
        executor.shutdown();
        try {
            executor.awaitTermination(flushTime, TimeUnit.MILLISECONDS);
            if (! executor.isTerminated()) {
                addWarn("Max queue flush timeout (" + flushTime + " ms) exceeded. Approximately "
                        + executor.getActiveCount() +
                        " queued task were possibly discarded.");
                flush();
            } else
                addInfo("Queue flush finished successfully within timeout.");

        } catch (InterruptedException e) {
            addError("Failed to join worker thread. " + executor.getActiveCount() + " queued task may be discarded.", e);
            flush();
        }

    }

    protected void flush() {
        BlockingQueue<Runnable> q = executor.getQueue();
        while (!q.isEmpty()) {
            try {
                Runnable r = q.poll();
                r.run();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
//-----------------------------------------------------------------------------
//      properties to be set;

    public int getFlushTime() {
        return flushTime;
    }

    public void setFlushTime(int flushTime) {
        this.flushTime = flushTime;
    }

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }
    public void setExecutor(ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    public int getMaxThreadNum() {
        return maxThreadNum;
    }

    public void setMaxThreadNum(int maxThreadNum) {
        this.maxThreadNum = maxThreadNum;
    }

    public Appender<E> getBackupAppender() {
        return backupAppender;
    }

    public void setBackupAppender(Appender<E> backupAppender) {
        this.backupAppender = backupAppender;
    }

//-----------------------------------------------------------------------------
//      implementation for AppenderAttachable

    protected void passOnEvent(E e) {
        for (Appender app : appList)
            try {
                app.doAppend(e);
            } catch (Throwable t) {
                err.print("error append event " + e + "to appender " + app);
            }
    }


    CopyOnWriteArrayList<Appender<E>> appList = new CopyOnWriteArrayList<>();

    @Override
    public void addAppender(@Nonnull Appender<E> newAppender) {
        appList.add(newAppender);
    }

    @Override
    public Iterator<Appender<E>> iteratorForAppenders() {
        return appList.iterator();
    }

    @Override public
    @Nullable Appender<E> getAppender(String name) {
        for (Appender<E> app : appList)
            if (app.getName().equals(name))
                return app;
        return null;
    }

    @Override
    public boolean isAttached(Appender<E> appender) {
        for (Appender<E> a : appList) {
            if (a == appender)
                return true;
        }
        return false;
    }

    @Override
    public void detachAndStopAllAppenders() {
        for (Appender<E> a : appList) {
            a.stop();
        }
        appList.clear();
    }

    @Override
    public boolean detachAppender(Appender<E> appender) {
        return appList.remove(appender);
    }

    @Override
    public boolean detachAppender(String name) {
        for (int i = 0; i < appList.size(); i++) {
            Appender<E> a = appList.get(i);
            if (a.getName().equals(name)) {
                appList.remove(i);
                return true;
            }
        }
        return false;
    }
}
