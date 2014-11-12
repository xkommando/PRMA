package com.caibowen.prma.store.dao.impl;

import com.caibowen.gplume.jdbc.JdbcSupport;
import com.caibowen.prma.store.dao.Int4DAO;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author BowenCai
 * @since 10-11-2014.
 */
public abstract class AbstractInt4DAO<V> extends JdbcSupport implements Int4DAO<V> {

    protected abstract boolean doPut(int key, V value);

    public boolean put(final int key, @Nonnull final V value) {
        return hasKey(key) ? update(key, value) : doPut(key, value);
    }

    public boolean putIfAbsent(final int key, @Nonnull final V value) {
        return hasKey(key) || doPut(key, value);
    }

    @Override
    public boolean putIfAbsent(@Nonnull final Map<Integer, V> map) {
        final TreeMap<Integer, V> nV = new TreeMap<>();
        for (Map.Entry<Integer, V> e : map.entrySet())
            if (! hasKey(e.getKey()))
                nV.put(e.getKey(), e.getValue());

        return putAll(nV);
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public boolean isStarted() {
        return getDataSource() != null;
    }

}
