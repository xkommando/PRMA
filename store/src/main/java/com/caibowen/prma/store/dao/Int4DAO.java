package com.caibowen.prma.store.dao;

import com.caibowen.gplume.common.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
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
    List<Integer>
    keys();

    @Nonnull
    List<V>
    values();

    @Nonnull
    List<Pair<Integer, V>>
    entries();

    @Nonnull boolean
    putIfAbsent(int key, @Nonnull final V value);


    @Nonnull boolean
    putAll(final Map<Integer, V> map);

    @Nonnull boolean
    update(int key, @Nonnull V value);


    @Nullable V
    remove(int key, boolean returnVal) throws SQLException;
}
