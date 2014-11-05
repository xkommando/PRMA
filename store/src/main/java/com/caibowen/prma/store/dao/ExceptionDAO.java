package com.caibowen.prma.store.dao;

import com.caibowen.prma.store.ExceptionDO;

import java.util.List;

/**
 * @author BowenCai
 * @since 23-10-2014.
 */
public interface ExceptionDAO {

    boolean hasKey(long hash);
    boolean insertIfAbsent(long eventId, Throwable[] prop) throws Exception;
    boolean insert(long eventId, List<ExceptionDO> vols) throws Exception;

}
