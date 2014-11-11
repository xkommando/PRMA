package com.caibowen.prma.spi;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @author BowenCai
 * @since 10-11-2014.
 */
public interface SimpleCacheProvider extends Serializable {

    boolean contains(@Nonnull Object key);

    void clear();

    @Nonnull
    List keys();

    @Nullable
    Object put(@Nonnull Object key, @Nonnull Object value);

    @Nullable
    Object putIfAbsent(@Nonnull Object key, @Nonnull Object value);

    void putAll(@Nonnull Map<Object, Object> vals);

    @Nullable
    Object get(@Nonnull Object key);

    @Nonnull
    Map<Object, Object> getAll(@Nonnull Collection<?> keys);

    @Nonnull
    Map<Object, Object> toMap();

    @Nullable
    Object remove(@Nonnull Object key, boolean returnVal);

    void removeAll(@Nonnull Collection<?> keys);

    @Nonnull
    Future<Boolean> containsAsync(@Nonnull Object key);

    void clearAsync();

    @Nonnull
    Future<List<Object>> keysAsync();


    @Nullable
    Future<Object> putAsync(@Nonnull Object key, @Nonnull Object value);

    @Nullable
    Future<Object> putIfAbsentAsync(@Nonnull Object key, @Nonnull Object value);

    void putAllAsync(@Nonnull Map<Object, Object> vals);

    @Nonnull
    Future<Object> getAsync(@Nonnull Object key);

    @Nonnull
    Future<Map<Object, Object>> getAllAsync(@Nonnull Collection<?> keys);

    @Nonnull
    Future<Object> removeAsync(@Nonnull Object key, boolean returnVal);

    @Nonnull
    Future<Map<Object, Object>> toMapAsync();

    void removeAllAsync(@Nonnull Collection<?> keys);

}
