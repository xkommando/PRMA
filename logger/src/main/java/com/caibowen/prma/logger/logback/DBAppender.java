package com.caibowen.prma.logger.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.caibowen.prma.api.model.EventVO;
import com.caibowen.prma.logger.logback.LogbackEventAdaptor;
import com.caibowen.prma.store.rdb.EventPersistImpl;

import javax.inject.Inject;
import java.util.ArrayList;

/**
 * @author BowenCai
 * @since 21-10-2014.
 */
public class DBAppender extends AsyncAppenderWrapper<ILoggingEvent> {


    @Inject
    EventPersistImpl persister;
    protected LogbackEventAdaptor adapter = new LogbackEventAdaptor();

    protected ArrayList<EventVO> buffer;
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
        buffer.add(adapter.from(eventObject));
        if (buffer.size() > writeCacheSize) {
            final ArrayList<EventVO> old = new ArrayList<>(buffer);
            buffer.clear();
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
        persister.persist(adapter.from(event));
    }

    @Override
    public void stop() {
        super.stop();

        // shut down my system
    }

}
