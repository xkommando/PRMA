package com.caibowen.prma.logger.logback;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.spi.AppenderAttachable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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


    // event queue size
    public static final int DEFUALT_QUEUE_SIZE = 256;
    private int queueSize = DEFUALT_QUEUE_SIZE;

    // number of thread to append events.
    public static final int DEFAULT_THREAD_NUM = 1;
    private int threadNumber = DEFAULT_THREAD_NUM;

    private ThreadPoolExecutor executor;

    private ArrayBlockingQueue<E> eventQ;


    @Override
    public void start() {
        if (appList.size() == 0) {
            addError("No attached appender");
            return;
        }
        if (queueSize < 1) {
            addError("Invalid queue size [" + queueSize + "]");
            return;
        }
        eventQ = new ArrayBlockingQueue<E>(queueSize);

        executor  = new ThreadPoolExecutor(threadNumber, threadNumber,
                10, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());

        executor.execute(new Runnable() {
            @Override
            public void run() {

                while (started == true) {
                    try {
                        E e = eventQ.take();
                        passOnEvent(e);
                    } catch (Throwable t) {
                        err.print("error fetching event from queue");
                        continue;
                    }
                }
                addInfo("executor will flush remaining events before exiting. ");

                for (E e : eventQ) {
                    try {
                        passOnEvent(e);
                    } catch (Throwable t) {
                        err.print("error fetching event from queue");
                        continue;
                    }
                }
                appList.clear();
            }
        });

        super.start();
    }

    @Override
    protected void append(E eventObject) {

        // to avoid queue being blocked.
        if (eventQ.size() >= queueSize) {
            err.print("queue exceeded, overflow event " + eventObject);
            return;
        }

        try {
            eventQ.put(eventObject);
        } catch (InterruptedException e) {
            err.println("error adding event to queue");
            e.printStackTrace(err);
        }
    }


    @Override
    public void stop() {
        if (started == false)
            return;
        started = false;
        super.stop();

        try {
            executor.awaitTermination(flushTime, TimeUnit.MILLISECONDS);
            executor.shutdown();
            if (! executor.isTerminated())
                addWarn("Max queue flush timeout (" + flushTime + " ms) exceeded. Approximately "
                        + eventQ.size() +
                        " queued events were possibly discarded.");
            else
                addInfo("Queue flush finished successfully within timeout.");

        } catch (InterruptedException e) {
            addError("Failed to join worker thread. " + eventQ.size() + " queued events may be discarded.", e);
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

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public int getThreadNumber() {
        return threadNumber;
    }

    public void setThreadNumber(int threadNumber) {
        this.threadNumber = threadNumber;
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
