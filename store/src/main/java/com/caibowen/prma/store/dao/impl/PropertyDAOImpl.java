package com.caibowen.prma.store.dao.impl;

import com.caibowen.gplume.common.Pair;
import com.caibowen.gplume.jdbc.JdbcSupport;
import com.caibowen.gplume.jdbc.StatementCreator;
import com.caibowen.gplume.jdbc.mapper.RowMapping;
import com.caibowen.prma.store.dao.PropertyDAO;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author BowenCai
 * @since 24-10-2014.
 */
public class PropertyDAOImpl extends AbstractInt4DAO<Pair<String, String> > implements PropertyDAO {


    @Override
    public boolean hasKey(final int hash) {
        return queryForObject(new StatementCreator() {
            @Nonnull
            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
                return con.prepareStatement(
                        "SELECT count(1) FROM `property` WHERE id = " + hash);
            }
        }, RowMapping.BOOLEAN_ROW_MAPPING);
    }

    @Override
    public boolean hasVal(@Nonnull Pair<String, String> val) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Nullable
    @Override
    public Pair<String, String> get(int key) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Nonnull
    @Override
    public List<Integer> keys() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Nonnull
    @Override
    public List<Pair<String, String>> values() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Nonnull
    @Override
    public Map<Integer, Pair<String, String>> entries() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public boolean putAll(@Nonnull Map<Integer, Pair<String, String>> map) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public boolean update(int key, @Nonnull Pair<String, String> value) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Nullable
    @Override
    public Pair<String, String> remove(int key, boolean returnVal) throws SQLException {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    protected boolean doPut(int key, Pair<String, String> value) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public boolean insertIfAbsent(final long eventId, final Map<String, String> prop) {

        Iterator iter = prop.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry pairs = (Map.Entry) iter.next();
            if (hasKey(pairs.getKey().hashCode()))
                iter.remove();
        }
        return prop.isEmpty() || insertAll(eventId, prop);
    }

    @Override
    public boolean insertAll(final long eventId, final Map<String, String> prop) {

        batchInsert(new StatementCreator() {
            @Nonnull
            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT IGNORE INTO `property`(`id`, `key`, `value`)VALUES (?,?,?)");
                for (Map.Entry<String, String> e : prop.entrySet()) {
                    ps.setInt(1, e.getKey().hashCode());
                    ps.setString(2, e.getKey());
                    ps.setBytes(3, e.getValue().getBytes());
                    ps.addBatch();
                }
                return ps;
            }
        }, null, null);

        batchInsert(new StatementCreator() {
            @Nonnull
            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO `j_event_prop`(`prop_id`, `event_id`)VALUES (?,?)");
                for (Map.Entry<String, String> e : prop.entrySet()) {
                    ps.setInt(1, e.getKey().hashCode());
                    ps.setLong(2, eventId);
                    ps.addBatch();
                }
                return ps;
            }
        }, null, null);

        return true;
    }

}
