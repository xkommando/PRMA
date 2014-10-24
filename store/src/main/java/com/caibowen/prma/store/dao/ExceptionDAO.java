package com.caibowen.prma.store.dao;

import ch.qos.logback.classic.spi.IThrowableProxy;

/**
 * @author BowenCai
 * @since 23-10-2014.
 */
public interface ExceptionDAO {


    boolean insert(long eventId, IThrowableProxy prop);
}
