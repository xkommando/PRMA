package com.caibowen.prma.logger.dao;

import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import com.caibowen.prma.common.Hashing;

import java.util.Map;

/**
 * @author BowenCai
 * @since 23-10-2014.
 */
public interface ExceptionDAO {


    boolean insert(long eventId, IThrowableProxy prop);
}
