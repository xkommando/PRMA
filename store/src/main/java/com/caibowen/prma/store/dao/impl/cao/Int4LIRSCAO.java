package com.caibowen.prma.store.dao.impl.cao;

import com.caibowen.gplume.cache.mem.Int4LIRSCache;
import com.caibowen.gplume.misc.Assert;
import com.caibowen.prma.store.dao.Int4DAO;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * for
 * level 1 exception message, stack trace, property, marker
 * level 2 exception, event
 *
 *
 * @author BowenCai
 * @since 26-10-2014.
 */
public class Int4LIRSCAO<V> implements Int4DAO<V> {

    @Inject Int4DAO<V> db;

    protected Int4LIRSCache<V> cache;

    @Inject int writeCacheSize;
    protected HashMap<Integer, V> buffer = new HashMap<>(64);
    protected ReadWriteLock lock = new ReentrantReadWriteLock();


    @Override
    public void start() {
        Assert.notNull(db);
        db.start();
        cache = new Int4LIRSCache<>(-1, -1);
    }

    @Override
    public void stop() {
        if (! buffer.isEmpty()) {
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
        lock.readLock().lock();
        try {
            return cache.containsKey(key) || db.hasKey(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean hasVal(@Nonnull V val) {
        lock.readLock().lock();
        try {
            return cache.containsValue(val) || db.hasVal(val);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Nullable
    @Override
    public V get(int key) {
        lock.readLock().lock();
        try {
            V v = cache.get(key);
            if (v == null) {
                v = db.get(key);
                if (v != null)
                    cache.put(key, v); // cache is thread safe.
            }
            return v;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Nonnull
    @Override
    public List<Integer> keys() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(db.keys());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Nonnull
    @Override
    public List<V> values() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(db.values());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Nonnull
    @Override
    public Map<Integer, V> entries() {
        lock.readLock().lock();
        try {
            return Collections.unmodifiableMap(db.entries());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean put(int key, @Nonnull V value) {
        lock.writeLock().lock();
        try {
            cache.put(key, value);
            buffer.put(key, value);
            if (buffer.size() < writeCacheSize)
                return true;

            // batch insert
            boolean ok = db.putAll(buffer);
            if (!ok) {
                for (Map.Entry<Integer, V> e : buffer.entrySet())
                    cache.remove(e.getKey());
            }
            buffer.clear();
            return ok;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean putIfAbsent(int key, @Nonnull V value) {
        if (cache.containsKey(key))
            return true;
        boolean ret;
        lock.writeLock().lock();
        try {
            cache.put(key, value);
            buffer.put(key, value);
            if (buffer.size() < writeCacheSize)
                return true;
            // batch insert
            ret = db.putAll(buffer);
            if (!ret)
                for (Map.Entry<Integer, V> e : buffer.entrySet())
                    cache.remove(e.getKey());
            buffer.clear();
            return ret;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean putAll(@Nonnull Map<Integer, V> map) {
        boolean ok = false;
        lock.writeLock().lock();
        try {
            ok =db.putAll(map);
            if (ok)
                cache.putAll(map);
        } finally {
            lock.writeLock().unlock();
        }
        return ok;
    }

    @Override
    public boolean putIfAbsent(@Nonnull Map<Integer, V> values) {
        for (Map.Entry<Integer, V> e : values.entrySet()) {
            Integer k = e.getKey();
            V va = e.getValue();
            if (null == cache.put(k, va) && !hasKey(k))
                buffer.put(k, va);
        }
        boolean ret;
        if (buffer.size() < writeCacheSize)
            return true;
        synchronized (db) {
            ret = db.putAll(buffer);
            if (!ret)
                for (Map.Entry<Integer, V> e2 : buffer.entrySet())
                    cache.remove(e2.getKey());
            buffer.clear();
            return ret;
        }
    }

    @Override
    public boolean update(int key, @Nonnull V value) {
        boolean ok = true;
        V ov = cache.get(key);
        if (!value.equals(ov)) {
            lock.writeLock().lock();
            try {
                ok = db.update(key, value);
                if (ok)
                    cache.put(key, value);
            } finally {
                lock.writeLock().unlock();
            }
        }
        return ok;
    }

    @Nullable
    @Override
    public V remove(int key, boolean returnVal) throws SQLException {
        V ov = cache.get(key);
        returnVal = returnVal && ov == null;
        lock.writeLock().lock();
        try {
            if (returnVal)
                ov = db.remove(key, false);
            else
                db.remove(key, false);
            cache.remove(key);
        } finally {
            lock.writeLock().unlock();
        }
        return ov;
    }
}
