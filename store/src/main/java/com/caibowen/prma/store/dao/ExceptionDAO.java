package com.caibowen.prma.store.dao;

import ch.qos.logback.classic.spi.IThrowableProxy;
import com.caibowen.prma.store.ExceptionDO;

import java.util.List;

/**
 * @author BowenCai
 * @since 23-10-2014.
 */
public interface ExceptionDAO {

    boolean hasKey(long hash);
    boolean insert(long eventId, List<ExceptionDO> vols) throws Exception;
    boolean insert(long eventId, IThrowableProxy prop) throws Exception;

    boolean insertIfAbsent(long eventId, IThrowableProxy prop) throws Exception;
}
