package com.caibowen.prma.store.rdb.dao.impl;

import com.caibowen.gplume.common.Pair;
import com.caibowen.gplume.jdbc.StatementCreator;
import com.caibowen.gplume.jdbc.mapper.RowMapping;
import com.caibowen.prma.core.StrLoader;
import com.caibowen.prma.store.rdb.dao.PropertyDAO;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author BowenCai
 * @since 24-10-2014.
 */
public class PropertyDAOImpl extends AbstractInt4DAO<Pair<String, String> > implements PropertyDAO {

    @Inject
    final StrLoader sqls;

    public PropertyDAOImpl(StrLoader loader) {
        this.sqls = loader;
    }

    @Override
    public Map<String, Object> getByEvent(final long eventId) {
        List<Pair<String, String>> ls = queryForList(new StatementCreator() {
            @Nonnull
            @Override
            public PreparedStatement createStatement(@Nonnull Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(sqls.get("PropertyDAO.getByEvent"));
                ps.setLong(1, eventId);
                return ps;
            }
        }, PAIR_MAPPING);
        TreeMap m = new TreeMap();
        for (Pair<String, String> p : ls)
            m.put(p.first, p.second);
        return m;
    }

    @Override
    public boolean hasKey(final int hash) {
        return queryForObject(new StatementCreator() {
            @Nonnull
            @Override
            public PreparedStatement createStatement(@Nonnull Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "SELECT count(1) FROM `property` WHERE id=?");
                ps.setInt(1, hash);
                return ps;
            }
        }, RowMapping.BOOLEAN_ROW_MAPPING);
    }



    @Nullable
    @Override
    public Pair<String, String> get(final int key) {
        return queryForObject(new StatementCreator() {
            @Nonnull
            @Override
            public PreparedStatement createStatement(@Nonnull Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "SELECT `key`,`value` FROM `property` WHERE `id`=?");
                ps.setInt(1, key);
                return ps;
            }
        }, PAIR_MAPPING);
    }


    @Override
    protected boolean doPut(final int key, final Pair<String, String> value) {
        insert(new StatementCreator() {
            @Nonnull
            @Override
            public PreparedStatement createStatement(@Nonnull Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        sqls.get("PropertyDAO.putMap"));
                ps.setInt(1, key);
                ps.setString(2, value.first);
                ps.setBytes(3, value.second.getBytes());
                return ps;
            }
        }, null, null);

        return true;
    }

    @Override
    public boolean insertIfAbsent(final long eventId, final Map<String, String> prop) {
        TreeMap<String, String> map = new TreeMap<>();
        for (Map.Entry<String, String> e : prop.entrySet()) {
            if (! hasKey(e.getKey().hashCode()))
                map.put(e.getKey(), e.getValue());
        }

        if (! map.isEmpty())
            putMap(map);
        putRelation(eventId, prop);
        return true;
    }

    public void putMap(@Nonnull final Map<String, String> prop) {
        batchInsert(new StatementCreator() {
            @Nonnull
            @Override
            public PreparedStatement createStatement(@Nonnull Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        sqls.get("PropertyDAO.putMap"));
                for (Map.Entry<String, String> e : prop.entrySet()) {
                    ps.setInt(1, e.getKey().hashCode());
                    ps.setString(2, e.getKey());
                    ps.setBytes(3, e.getValue().getBytes());
                    ps.addBatch();
                }
                return ps;
            }
        }, null, null);
    }

    protected void putRelation(final long eventId, final Map<String, String> prop) {
        batchInsert(new StatementCreator() {
            @Nonnull
            @Override
            public PreparedStatement createStatement(@Nonnull Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        sqls.get("PropertyDAO.putRelation"));
                for (Map.Entry<String, String> e : prop.entrySet()) {
                    ps.setInt(1, e.getKey().hashCode());
                    ps.setLong(2, eventId);
                    ps.addBatch();
                }
                return ps;
            }
        }, null, null);
    }

    public boolean putAll(@Nonnull final Map<Integer, Pair<String, String>> prop) {
        batchInsert(new StatementCreator() {
            @Nonnull
            @Override
            public PreparedStatement createStatement(@Nonnull Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        sqls.get("PropertyDAO.putMap"));
                for (Pair<String, String> e : prop.values()) {
                    ps.setInt(1, e.first.hashCode());
                    ps.setString(2, e.first);
                    ps.setBytes(3, e.second.getBytes());
                    ps.addBatch();
                }
                return ps;
            }
        }, null, null);
        return true;
    }


    @Override
    public boolean insertAll(final long eventId, final Map<String, String> prop) {
        putMap(prop);
        putRelation(eventId, prop);
        return true;
    }

    public static final RowMapping<Pair<String, String>> PAIR_MAPPING = new RowMapping<Pair<String, String>>() {
        @Override
        public Pair<String, String> extract(@Nonnull ResultSet rs) throws SQLException {
            return new Pair<>(rs.getString(1), rs.getString(2));
        }
    };

    @Override
    public boolean hasVal(@Nonnull Pair<String, String> val) {
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
}
