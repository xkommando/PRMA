package com.caibowen.prma.spi;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @author BowenCai
 * @since 11-11-2014.
 */
public interface Int4CacheProvider {

    boolean contains(int key);

    void clear();

    @Nonnull
    List<Integer> keys();

    @Nullable
    Object put(int key, @Nonnull Object value);

    @Nullable
    Object putIfAbsent(int key, @Nonnull Object value);

    void putAll(@Nonnull Map<Integer, Object> vals);

    @Nullable
    Object get(int key);

    @Nonnull
    Map<Integer, Object> getAll(@Nonnull Collection<Integer> keys);

    @Nonnull
    Map<Integer, Object> toMap();

    @Nullable
    Object remove(int key, boolean returnVal);

    void removeAll(@Nonnull Collection<Integer> keys);

    @Nonnull
    Future<Boolean> containsAsync(int key);

    void clearAsync();

    @Nonnull
    Future<List<Integer>> keysAsync();


    @Nullable
    Future<Object> putAsync(int key, @Nonnull Object value);

    @Nullable
    Future<Object> putIfAbsentAsync(int key, @Nonnull Object value);

    void putAllAsync(@Nonnull Map<Integer, Object> vals);

    @Nonnull
    Future<Object> getAsync(int key);

    @Nonnull
    Future<Map<Integer, Object>> getAllAsync(@Nonnull Collection<Integer> keys);

    @Nonnull
    Future<Object> removeAsync(int key, boolean returnVal);

    @Nonnull
    Future<Map<Integer, Object>> toMapAsync();

    void removeAllAsync(@Nonnull Collection<Integer> keys);
}
