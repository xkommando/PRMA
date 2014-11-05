package com.caibowen.prma.logger.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.caibowen.gplume.annotation.ConstMethod;
import com.caibowen.gplume.annotation.Internal;
import com.caibowen.prma.store.EventPersistImpl;

import javax.inject.Inject;
import java.util.ArrayList;

/**
 * @author BowenCai
 * @since 21-10-2014.
 */
public class DBAppender extends AsyncAppenderWrapper<ILoggingEvent> {


    @Inject
    EventPersistImpl persister;

    protected ArrayList<ILoggingEvent> cache;
    @Inject int writeCacheSize;
    public int getWriteCacheSize() {
        return writeCacheSize;
    }
    public void setWriteCacheSize(int writeCacheSize) {
        this.writeCacheSize = writeCacheSize;
    }

    @Override
    public void start() {
        super.start();

        // boot the my system.
    }

    @Override
    protected void append(final ILoggingEvent eventObject) {
        cache.add(eventObject);
        if (cache.size() > writeCacheSize) {
            final ArrayList<ILoggingEvent> old = new ArrayList<>(cache);
            cache.clear();
            super.executor.execute(new Runnable() {
                @Override
                public void run() {
                    persister.batchPersist(old);
                }
            });
        }
    }


    @Override
    protected void passOnEvent(ILoggingEvent event) {
        persister.persist(event);
    }

    @Override
    public void stop() {
        super.stop();

        // shut down my system
    }

}
