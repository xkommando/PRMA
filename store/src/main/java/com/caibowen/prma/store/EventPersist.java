package com.caibowen.prma.store;

import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.List;

/**
 * @author BowenCai
 * @since 27-10-2014.
 */
public interface EventPersist {

    void persist(ILoggingEvent event);

    void batchPersist(List<ILoggingEvent> ls);
}
