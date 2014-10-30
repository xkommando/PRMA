package com.caibowen.prma.store;

import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * @author BowenCai
 * @since 27-10-2014.
 */
public interface EventPersist {

    void persist(ILoggingEvent event);
}
