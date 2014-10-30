package com.caibowen.prma.store.dao.impl.cao;

import com.caibowen.prma.store.dao.Int4DAO;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * all data is stored in the memory, read operations only reach the memory
 * when write data, the value is write through to the DB.
 * <p/>
 * for logger name, thread name, exception name
 *
 * @author BowenCai
 * @since 26-10-2014.
 */
public class Int4FullCAO<V> implements Int4DAO<V> {

    @Inject
    Int4DAO<V> db;

    private Map<Integer, V> mem = new HashMap<>(256);
    private ReadWriteLock prepare = new ReentrantReadWriteLock();

    synchronized public void init() {
        mem.putAll(db.entries());
    }

    @Override
    public boolean hasKey(int key) {
        prepare.readLock().lock();
        try {
            return mem.containsKey(key);
        } finally {
            prepare.readLock().unlock();
        }
    }

    @Override
    public boolean hasVal(@Nonnull V val) {
        prepare.readLock().lock();
        try {
            return mem.containsValue(val);
        } finally {
            prepare.readLock().unlock();
        }
    }

    @Nullable
    @Override
    public V get(int key) {
        prepare.readLock().lock();
        try {
            return mem.get(key);
        } finally {
            prepare.readLock().unlock();
        }
    }

    @Nonnull
    @Override
    public List<Integer> keys() {
        prepare.readLock().lock();
        try {
            return new ArrayList<>(mem.keySet());
        } finally {
            prepare.readLock().unlock();
        }
    }

    @Nonnull
    @Override
    public List<V> values() {
        prepare.readLock().lock();
        try {
            return new ArrayList<>(mem.values());
        } finally {
            prepare.readLock().unlock();
        }
    }

    @Nonnull
    @Override
    public Map<Integer, V> entries() {
        prepare.readLock().lock();
        try {
            return Collections.unmodifiableMap(mem);
        } finally {
            prepare.readLock().unlock();
        }
    }

    @Nonnull
    @Override
    public boolean put(int key, @Nonnull V value) {
        prepare.writeLock().lock();
        try {
            boolean ok = db.put(key, value);
            if (ok)
                mem.put(key, value);
            return ok;
        } finally {
            prepare.writeLock().unlock();
        }

    }

    @Override
    public boolean putIfAbsent(int key, @Nonnull V value) {
        boolean ret = true;
        if (!mem.containsKey(key)) {
            prepare.writeLock().lock();
            try {
                ret = db.putIfAbsent(key, value);
                if (ret)
                    mem.put(key, value);
            } finally {
                prepare.writeLock().unlock();
            }
        }
        return ret;
    }

    @Override
    public boolean putAll(@Nonnull Map<Integer, V> map) {
        boolean ok = false;
        prepare.writeLock().lock();
        try {
            ok =db.putAll(map);
            if (ok)
                mem.putAll(map);
        } finally {
            prepare.writeLock().unlock();
        }
        return ok;
    }

    @Nonnull
    @Override
    public boolean update(int key, @Nonnull V value) {
        boolean ok = true;
        V ov = mem.get(key);
        if (!value.equals(ov)) {
            prepare.writeLock().lock();
            try {
                ok = db.update(key, value);
                if (ok)
                    mem.put(key, value);
            } finally {
                prepare.writeLock().unlock();
            }
        }
        return ok;
    }

    @Nullable
    @Override
    public V remove(int key, boolean returnVal) throws SQLException {
        V ov = mem.get(key);
        if (ov != null) {
            prepare.writeLock().lock();
            try {
                db.remove(key, false);
                mem.remove(key);
            } finally {
                prepare.writeLock().unlock();
            }
        }
        return returnVal ? ov : null;
    }
}
