package com.caibowen.prma.store.rdb.dao.impl.cao;

import com.caibowen.prma.spi.Int4CacheProvider;
import com.caibowen.gplume.misc.Assert;
import com.caibowen.prma.store.rdb.dao.Int4DAO;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * for
 * level 1 exception message, stack trace, property, marker
 * level 2 exception, event
 *
 * @author BowenCai
 * @since 26-10-2014.
 */
public class Int4LIRSCAO<V> implements Int4DAO<V> {

    @Inject
    Int4DAO<V> db;

    protected Int4CacheProvider cache;

    @Inject
    int writeBufferSize;

    protected ConcurrentHashMap<Integer, V> buffer = new ConcurrentHashMap<>(64);

    @Override
    public void start() {
        Assert.notNull(db);
        db.start();
    }

    @Override
    public void stop() {
        if (!buffer.isEmpty()) {
            db.putAll(buffer);
            buffer.clear();
        }
        db.stop();
    }

    @Override
    public boolean isStarted() {
        return db.isStarted();
    }


    @Override
    public boolean hasKey(int key) {
        return cache.contains(key) || db.hasKey(key);
    }

    @Override
    public boolean hasVal(@Nonnull V val) {
        return db.hasVal(val);
    }

    @Nullable
    @Override
    public V get(int key) {
        V v = (V) cache.get(key);
        if (v == null) {
            synchronized (db) {
                v = db.get(key);
                if (v != null)
                    cache.put(key, v); // cache is thread safe.
            }
        }
        return v;
    }

    @Nonnull
    @Override
    public List<Integer> keys() {
        return new ArrayList<>(db.keys());
    }

    @Nonnull
    @Override
    public List<V> values() {
        return new ArrayList<>(db.values());
    }

    @Nonnull
    @Override
    public Map<Integer, V> entries() {
        return Collections.unmodifiableMap(db.entries());
    }

    @Override
    public boolean put(int key, @Nonnull V value) {
        cache.put(key, value);
        buffer.put(key, value);
        if (buffer.size() < writeBufferSize)
            return true;

        synchronized (db) {
            // batch insert
            boolean ok = db.putAll(buffer);
            if (!ok) {
                for (Map.Entry<Integer, V> e : buffer.entrySet())
                    cache.remove(e.getKey(), false);
            }
            buffer.clear();
            return ok;
        }
    }

    @Override
    public boolean putIfAbsent(int key, @Nonnull V value) {
        if (cache.contains(key) || db.hasKey(key))
            return true;
        boolean ret;
        cache.put(key, value);
        buffer.put(key, value);
        if (buffer.size() < writeBufferSize)
            return true;
        // batch insert
        synchronized (db) {
            ret = db.putAll(buffer);
            if (!ret)
                for (Map.Entry<Integer, V> e : buffer.entrySet())
                    cache.remove(e.getKey(), false);
            buffer.clear();
            return ret;
        }
    }

    @Override
    public boolean putAll(@Nonnull Map<Integer, V> map) {
        synchronized (db) {
            boolean ok = db.putAll(map);
            if (ok)
                cache.putAll((Map<Integer, Object>) map);
            return ok;
        }
    }

    @Override
    public boolean putIfAbsent(@Nonnull Map<Integer, V> values) {
        for (Map.Entry<Integer, V> e : values.entrySet()) {
            Integer k = e.getKey();
            V va = e.getValue();
            if (null == cache.putIfAbsent(k, va) && !db.hasKey(k))
                buffer.put(k, va);
        }
        boolean ret;
        if (buffer.size() < writeBufferSize)
            return true;
        synchronized (db) {
            ret = db.putAll(buffer);
            if (!ret)
                for (Map.Entry<Integer, V> e2 : buffer.entrySet())
                    cache.remove(e2.getKey(), false);
            buffer.clear();
            return ret;
        }
    }

    @Override
    public boolean update(int key, @Nonnull V value) {
        boolean ok = true;
        V ov = (V) cache.get(key);
        if (!value.equals(ov)) {
            synchronized (db) {
                ok = db.update(key, value);
                if (ok)
                    cache.put(key, value);
            }
        }
        return ok;
    }

    @Nullable
    @Override
    public V remove(int key, boolean returnVal) throws SQLException {
        V ov = (V) cache.get(key);
        returnVal = returnVal && ov == null;
        synchronized (db) {
            if (returnVal)
                ov = db.remove(key, false);
            else
                db.remove(key, false);
            cache.remove(key, false);
        }
        return ov;
    }

    // scala trash
    @Override
    public boolean started() {throw new UnsupportedOperationException("use isStarted");}
    @Override
    public void started_$eq(boolean started) {throw new UnsupportedOperationException();}
}
