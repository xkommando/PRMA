package com.caibowen.prma.store.dao.impl.cao;

import com.caibowen.gplume.cache.mem.Int4LIRSCache;
import com.caibowen.prma.store.dao.Int4DAO;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * lefvel 1 exception message, stack trace, exception, property
 * level 2 exception, event
 *
 * @author BowenCai
 * @since 26-10-2014.
 */
public class Int4LIRSCAO<V> implements Int4DAO<V> {

    @Inject Int4DAO<V> db;

    private Int4LIRSCache<V> cache;

    public void init() {

    }


    @Override
    public boolean hasKey(int key) {
        return false;
    }

    @Override
    public boolean hasVal(@Nonnull V val) {
        return false;
    }

    @Nullable
    @Override
    public V get(int key) {
        return null;
    }

    @Nonnull
    @Override
    public List<Integer> keys() {
        return null;
    }

    @Nonnull
    @Override
    public List<V> values() {
        return null;
    }

    @Nonnull
    @Override
    public Map<Integer, V> entries() {
        return null;
    }

    @Nonnull
    @Override
    public boolean put(int key, @Nonnull V value) {
        boolean ok = db.put(key, value);
        if (ok)
            cache.put(key, value);
        return ok;
    }

    @Override
    public boolean putIfAbsent(int key, @Nonnull V value) {
        return false;
    }

    @Override
    public boolean putAll(@Nonnull Map<Integer, V> map) {
        return false;
    }

    @Override
    public boolean update(int key, @Nonnull V value) {
        return false;
    }

    @Nullable
    @Override
    public V remove(int key, boolean returnVal) throws SQLException {
        return null;
    }
}
