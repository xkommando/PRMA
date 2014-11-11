package com.caibowen.prma.core.memcache;

import com.caibowen.prma.spi.Int4CacheProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author BowenCai
 * @since 11-11-2014.
 */
public class Int4FullCache implements Int4CacheProvider {

    private static final long serialVersionUID = -3202125346057371631L;

    private ConcurrentHashMap<Integer, Object> cache;
    public Int4FullCache() {
        cache = new ConcurrentHashMap<>(128);
    }

    public Int4FullCache(int initSz) {
        this.cache = new ConcurrentHashMap<>(initSz * 4 / 3 + 1);
    }

    @Override
    public boolean contains(@Nonnull int key) {
        return cache.containsKey(key);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Nonnull
    @Override
    public List<Integer> keys() {
        return new ArrayList<>(cache.keySet());
    }

    @Override
    public Object put(@Nonnull int key, @Nonnull Object value) {
        return cache.put(key, value);
    }

    @Override
    public Object putIfAbsent(@Nonnull int key, @Nonnull Object value) {
        if (!cache.containsKey(key))
            return cache.put(key, value);
        return null;
    }

    @Override
    public void putAll(@Nonnull Map<Integer, Object> vals) {
        cache.putAll(vals);
    }

    @Override
    public Object get(@Nonnull int key) {
        return cache.get(key);
    }

    @Nonnull
    @Override
    public Map<Integer, Object> getAll(@Nonnull Collection<Integer> keys) {
        HashMap ret = new HashMap(keys.size() * 4 / 3 + 2);
        for (Object k : keys) {
            Object v = cache.get(k);
            if (v != null)
                ret.put(k, v);
        }
        return ret;
    }



    @Nonnull
    @Override
    public Map<Integer, Object> toMap() {
        return new HashMap<>(cache);
    }

    @Nullable
    @Override
    public Object remove(@Nonnull int key, boolean returnVal) {
        Object v = cache.remove(key);
        return returnVal ? v : null;
    }

    @Override
    public void removeAll(@Nonnull Collection<Integer> keys) {
        for (Object k : keys)
            cache.remove(k);
    }

    @Nonnull
    @Override
    public Future<Boolean> containsAsync(@Nonnull final int key) {
        return new FutureTask<>(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return Int4FullCache.this.cache.contains(key);
            }
        });
    }

    @Override
    public void clearAsync() {
        cache.clear();
    }

    @Nonnull
    @Override
    public Future<List<Integer>> keysAsync() {
        return new FutureTask<>(new Callable<List<Integer>>() {
            @Override
            public List<Integer> call() throws Exception {
                return new ArrayList<>(Int4FullCache.this.cache.keySet());
            }
        });
    }

    @Override
    public Future<Object> putAsync(@Nonnull final int key, @Nonnull final Object value) {
        return new FutureTask<Object>(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return cache.put(key, value);
            }
        });
    }

    @Nullable
    @Override
    public Future<Object> putIfAbsentAsync(final int key, @Nonnull final Object value) {
        return new FutureTask<Object>(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return cache.put(key, value);
            }
        });
    }

    @Override
    public void putAllAsync(@Nonnull Map<Integer, Object> vals) {
        cache.putAll(vals);
    }

    @Nonnull
    @Override
    public Future<Object> getAsync(@Nonnull final int key) {
        return new FutureTask<>(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return Int4FullCache.this.cache.get(key);
            }
        });
    }

    @Nonnull
    @Override
    public Future<Map<Integer, Object>> getAllAsync(@Nonnull final Collection<Integer> keys) {
        return new FutureTask<>(new Callable<Map<Integer, Object>>() {
            @Override
            public Map<Integer, Object> call() throws Exception {
                return Int4FullCache.this.getAll(keys);
            }
        });
    }

    @Nonnull
    @Override
    public Future<Map<Integer, Object>> toMapAsync() {
        return new FutureTask<Map<Integer, Object>>(new Callable<Map<Integer, Object>>() {
            @Override
            public Map<Integer, Object> call() throws Exception {
                return toMap();
            }
        });
    }

    @Nonnull
    @Override
    public Future<Object> removeAsync(@Nonnull final int key, final boolean returnVal) {
        return new FutureTask<>(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                Object v = Int4FullCache.this.cache.remove(key);
                return returnVal ? v : null;
            }
        });
    }

    @Override
    public void removeAllAsync(@Nonnull Collection<Integer> keys) {
        removeAll(keys);
    }
}
