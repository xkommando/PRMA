package com.caibowen.prma.logger.db;

import javax.annotation.Nonnull;

/**
 * @author BowenCai
 * @since 22-10-2014.
 */
public interface EventDAO {

//    int insertIfAbsent();

    /**
     * if exists, return id, or insert and return id
     * @param st
     * @return
     */
    int getStackTraceId(@Nonnull StackTraceElement st);

    int getLoggerId(@Nonnull String loggerName);
    int getThreadId(@Nonnull String threadName);

    /**
     *
     * @param key
     * @param val
     * @param insertAbsent if value already exists whether update it.
     *
     * @return if value already exists, true to update, false not update.
     */
    int insertProperty(String key, Object val, boolean insertAbsent);

    /**
     *
     * @param key
     * @param val
     * @return
     */
    int insertOrUpdateProperty(String key, Object val);

}










