package com.caibowen.prma.store.dao;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 *
 * in the database, the actual key is not stored,
 * the hashCode() of the actual key is inserted as the id of the value.
 *
 * WARN:
 * 1. a 32-bit int is used to identify a string key, possible hash collision !
 * 2. the hashCode() of some java class, e.g., java.lang.String, is not standardized,
 *      and is implementation dependent.
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
    List<Integer>
    keys();

    @Nonnull
    List<V>
    values();

    @Nonnull
    Map<Integer, V>
    entries();

    @Nonnull boolean
    putIfAbsent(int key, @Nonnull final V value);


    boolean
    putAll(@Nonnull final Map<Integer, V> map);

    @Nonnull boolean
    update(int key, @Nonnull V value);


    @Nullable V
    remove(int key, boolean returnVal) throws SQLException;
}
