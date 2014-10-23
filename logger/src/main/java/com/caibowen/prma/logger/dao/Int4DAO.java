package com.caibowen.prma.logger.dao;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

/**
 *
 * WARN: a 32-bit int is used to identify a string key, possible hash collision !
 *
 * @author BowenCai
 * @since 22-10-2014.
 */
public interface Int4DAO<V> {

    boolean
    hasKey(int key);

    boolean
    hasVal(@Nonnull final V val);

    @Nullable
    V
    get(int key);

    @Nonnull
    Set<Integer>
    keys();

    @Nonnull Set<V>
    values();

    @Nonnull boolean
    putIfAbsent(int key, @Nonnull final V value);


    @Nonnull boolean
    putAll(final Map<Integer, V> map);

    @Nonnull boolean
    update(int key, @Nonnull V value);


    @Nullable V
    remove(int key, boolean returnVal) throws SQLException;
}
