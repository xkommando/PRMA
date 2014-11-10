package com.caibowen.prma.store.dao.impl.cao;

import com.caibowen.gplume.annotation.ConstMethod;
import com.caibowen.gplume.misc.Assert;
import com.caibowen.prma.store.dao.Int4DAO;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * all data is stored in the memory.
 * read operation done in memory
 * write operation is buffered.
 *
 * for logger name, thread name, exception name
 *
 * @author BowenCai
 * @since 26-10-2014.
 */
public class Int4FullCAO<V> implements Int4DAO<V>, Serializable {

    private static final long serialVersionUID = 4978434148771799147L;

    @Inject
    Int4DAO<V> db;

    private Map<Integer, V> mem = new ConcurrentHashMap<>(256);
//    private ReadWriteLock lock = new ReentrantReadWriteLock();

    @Inject int writeCacheSize;
    protected Map<Integer, V> buffer = new ConcurrentHashMap<>(64);

    @Override
    public void start() {
        Assert.notNull(db);
        db.start();
        mem.putAll(db.entries());
    }

    @Override
    public void stop() {
        if (buffer.size() != 0) {
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
            return mem.containsKey(key);
    }

    @Override
    public boolean hasVal(@Nonnull V val) {
            return mem.containsValue(val);
    }

    @Nullable
    @Override
    public V get(int key) {
            return mem.get(key);
    }

    /**
     *  return new array list of values.
     */
    @ConstMethod
    @Nonnull
    @Override
    public List<Integer> keys() {
            return new ArrayList<>(mem.keySet());
    }

    @Nonnull
    @Override
    public List<V> values() {
            return new ArrayList<>(mem.values());
    }

    @Nonnull
    @Override
    public Map<Integer, V> entries() {
            return Collections.unmodifiableMap(mem);
    }

    @Override
    public boolean put(int key, @Nonnull V value) {
            mem.put(key, value);
            buffer.put(key, value);
            if (buffer.size() < writeCacheSize)
                return true;
        synchronized (db) {
            // batch insert
            boolean ok = db.putAll(buffer);
            if (!ok) {
                for (Map.Entry<Integer, V> e : buffer.entrySet())
                    mem.remove(e.getKey());
            }
            buffer.clear();
            return ok;
        }
    }


    @Override
    public boolean putIfAbsent(@Nonnull Map<Integer, V> values) {
            for (Map.Entry<Integer, V> e : values.entrySet()) {
                Integer k = e.getKey();
                V va = e.getValue();
                if (null == mem.put(k, va))
                    buffer.put(k, va);
            }
            boolean ret;
            if (buffer.size() < writeCacheSize)
                return true;
        synchronized (db) {
            ret = db.putAll(buffer);
            if (!ret)
                for (Map.Entry<Integer, V> e2 : buffer.entrySet())
                    mem.remove(e2.getKey());
            buffer.clear();
            return ret;
        }
    }


    @Override
    public boolean putIfAbsent(int key, @Nonnull V value) {
        if (mem.containsKey(key))
            return true;

        boolean ret;
            mem.put(key, value);
            buffer.put(key, value);
            if (buffer.size() < writeCacheSize)
                return true;
            // batch insert
        synchronized (db) {
            ret = db.putAll(buffer);
            if (!ret)
                for (Map.Entry<Integer, V> e : buffer.entrySet())
                    mem.remove(e.getKey());
            buffer.clear();
            return ret;
        }
    }

    @Override
    synchronized public boolean putAll(@Nonnull Map<Integer, V> map) {
        boolean ok = db.putAll(map);
        if (ok)
            mem.putAll(map);
        return ok;
    }


    /**
     *
     * low frequency operation, no buffer
     */
    @Override
    public boolean update(int key, @Nonnull V value) {
        boolean ok = true;
        V ov = mem.get(key);
        if (!value.equals(ov)) {
            synchronized (db) {
                ok = db.update(key, value);
                if (ok)
                    mem.put(key, value);
            }
        }
        return ok;
    }

    /**
     *
     * low frequency operation, no buffer
     */
    @Nullable
    @Override
    public V remove(int key, boolean returnVal) throws SQLException {
        V ov = mem.get(key);
        if (ov != null) {
            synchronized (db){
                db.remove(key, false);
                mem.remove(key);
            }
        }
        return returnVal ? ov : null;
    }


    public int getWriteCacheSize() {
        return writeCacheSize;
    }

    public void setWriteCacheSize(int writeCacheSize) {
        this.writeCacheSize = writeCacheSize;
    }
}
