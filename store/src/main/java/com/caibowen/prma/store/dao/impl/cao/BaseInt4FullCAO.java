package com.caibowen.prma.store.dao.impl.cao;

import com.caibowen.gplume.common.Pair;
import com.caibowen.prma.store.dao.Int4DAO;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author BowenCai
 * @since 26-10-2014.
 */
public class BaseInt4FullCAO<V> implements Int4DAO<V> {

    @Inject Int4DAO<V> db;

    private Map<Integer, V> mem;
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    synchronized public void init() {
        List<Pair<Integer, V>> ls = db.entries();
        if (ls != null)
            mem = new HashMap<>(ls.size() * 4 / 3 + 2);
        for (Pair<Integer, V> p : ls)
            mem.put(p.first, p.second);
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
    public List<Pair<Integer, V>> entries() {
        Set<Map.Entry<Integer, V>> s = mem.entrySet();
        List<Pair<Integer, V>> ls = new ArrayList<>(s.size());
        for (Map.Entry<Integer, V> e : s)
            ls.add(new Pair<Integer, V>(e.getKey(), e.getValue()));

        return ls;
    }

    @Nonnull
    @Override
    public boolean putIfAbsent(int key, @Nonnull V value) {
        boolean ret = true;
        if (!mem.containsKey(key)) {
            ret = db.putIfAbsent(key, value);
            if (ret)
                mem.put(key, value);
        }
        return ret;
    }

    @Nonnull
    @Override
    public boolean putAll(Map<Integer, V> map) {
        return false;
    }

    @Nonnull
    @Override
    public boolean update(int key, @Nonnull V value) {
        boolean ok = true;
        V ov = mem.get(key);
        if (! value.equals(ov)) {
            ok = db.update(key, value);
            if (ok)
                mem.put(key, value);
        }
        return ok;
    }

    @Nullable
    @Override
    public V remove(int key, boolean returnVal) throws SQLException {
        V ov = mem.get(key);
        if (ov != null) {
            db.remove(key, false);
            mem.remove(key);
        }
        return returnVal ? ov : null;
    }
}
