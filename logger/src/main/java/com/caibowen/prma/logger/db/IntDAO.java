package com.caibowen.prma.logger.db;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

/**
 *
 * WARN: a 32-bit int is used to identify a string key, possible hash collision !
 *
 * @author BowenCai
 * @since 22-10-2014.
 */
public interface IntDAO<V> {

    boolean
    hasKey(int key);

    boolean
    hasVal(@Nonnull V val);

    @Nullable
    V
    get(int key);

    @Nonnull
    Set<Integer>
    keys();

    @Nonnull Set<V>
    values();

    @Nonnull boolean
    putIfAbsent(int key, @Nonnull V value);

    @Nonnull boolean
    update(int key, @Nonnull V value);

    @Nullable boolean
    remove(int key, boolean returnVal);
}