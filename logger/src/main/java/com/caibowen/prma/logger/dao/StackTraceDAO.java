package com.caibowen.prma.logger.dao;

import javax.annotation.Nonnull;

/**
 * @author BowenCai
 * @since 23-10-2014.
 */
public class StackTraceDAO {

    public static long hash(StackTraceElement st) {
        return 0L;
    }

    @Nonnull public boolean putIfAbsent(int key, @Nonnull final StackTraceElement value) {
        return true;
    }
}
