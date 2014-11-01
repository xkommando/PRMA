package com.caibowen.prma.logger.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.caibowen.prma.logger.logback.AsyncAppenderWrapper;
import com.caibowen.prma.store.EventPersistImpl;

import javax.inject.Inject;

/**
 * @author BowenCai
 * @since 21-10-2014.
 */
public class DBAppender extends AsyncAppenderWrapper<ILoggingEvent> {


    @Inject
    EventPersistImpl persister;


    @Override
    public void start() {
        super.start();

        // boot the my system.
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
