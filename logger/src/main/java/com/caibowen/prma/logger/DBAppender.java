package com.caibowen.prma.logger;

import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * @author BowenCai
 * @since 21-10-2014.
 */
public class DBAppender extends AsyncAppenderWrapper<ILoggingEvent> {


    EventPersist persister;


    @Override
    public void start() {
        super.start();

        // boot the my system.
    }

    @Override
    protected void passOnEvent(ILoggingEvent event) {
        persister.xxxxxxxx(event);
    }

    @Override
    public void stop() {
        super.stop();

        // shut down my system
    }

}
