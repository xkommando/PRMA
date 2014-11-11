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
public interface Int8CacheProvider {

    boolean contains(long key);

    void clear();

    @Nonnull
    List<Long> keys();

    @Nullable
    Object put(long key, @Nonnull Object value);

    @Nullable
    Object putIfAbsent(long key, @Nonnull Object value);

    void putAll(@Nonnull Map<Long, Object> vals);

    @Nullable
    Object get(long key);

    @Nonnull
    Map<Long, Object> getAll(@Nonnull Collection<?> keys);

    @Nonnull
    Map<Long, Object> toMap();

    @Nullable
    Object remove(long key, boolean returnVal);

    void removeAll(@Nonnull Collection<Long> keys);

    @Nonnull
    Future<Boolean> containsAsync(long key);

    void clearAsync();

    @Nonnull
    Future<List<Long>> keysAsync();


    @Nullable
    Future<Object> putAsync(long key, @Nonnull Object value);

    @Nullable
    Future<Object> putIfAbsentAsync(long key, @Nonnull Object value);

    void putAllAsync(@Nonnull Map<Long, Object> vals);

    @Nonnull
    Future<Object> getAsync(long key);

    @Nonnull
    Future<Map<Long, Object>> getAllAsync(@Nonnull Collection<?> keys);

    @Nonnull
    Future<Object> removeAsync(long key, boolean returnVal);

    @Nonnull
    Future<Map<Long, Object>> toMapAsync();

    void removeAllAsync(@Nonnull Collection<Long> keys);
}
