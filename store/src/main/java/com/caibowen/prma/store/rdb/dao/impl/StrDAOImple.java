package com.caibowen.prma.store.rdb.dao.impl;

import com.caibowen.gplume.common.Pair;
import com.caibowen.gplume.jdbc.StatementCreator;
import com.caibowen.gplume.jdbc.mapper.RowMapping;
import com.caibowen.prma.store.rdb.dao.Int4DAO;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author BowenCai
 * @since 23-10-2014.
 */
public class StrDAOImple extends AbstractInt4DAO<String> implements Int4DAO<String>, Serializable {

    private static final long serialVersionUID = 5288491677679354232L;

    public static final RowMapping<Pair<Integer, String> > PAIR_MAPPING = new RowMapping<Pair<Integer, String>>() {
        @Override
        public Pair<Integer, String> extract(@Nonnull ResultSet rs) throws SQLException {
            return new Pair<>(rs.getInt(1), rs.getString(2));
        }
    };

    public final String tableName;

    public StrDAOImple(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public boolean hasKey(final int key) {
        return queryForObject(new StatementCreator() {
            @Nonnull
            @Override
            public PreparedStatement createStatement(@Nonnull Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "SELECT count(1) FROM " + tableName + " WHERE id=?");
                ps.setInt(1, key);
                return ps;
            }
        }, RowMapping.BOOLEAN_ROW_MAPPING);
    }

    @Override
    public boolean hasVal(@Nonnull final String val) {
        return queryForObject(new StatementCreator() {
            @Nonnull
            @Override
            public PreparedStatement createStatement(@Nonnull Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "SELECT COUNT (1) FROM " + tableName + " WHERE `value` = ? ");
                ps.setString(1, val);
                return ps;
            }
        }, RowMapping.BOOLEAN_ROW_MAPPING);
    }

    @Nullable
    @Override
    public String get(final int key) {
        return queryForObject(new StatementCreator() {
            @Nonnull
            @Override
            public PreparedStatement createStatement(@Nonnull Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "SELECT `value` FROM " + tableName + " WHERE id=? LIMIT 1");
                ps.setInt(1, key);
                return ps;
            }
        }, RowMapping.STR_ROW_MAPPING);
    }

    @Nonnull
    @Override
    public List<Integer> keys() {
        return queryForList(new StatementCreator() {
            @Nonnull
            @Override
            public PreparedStatement createStatement(@Nonnull Connection con) throws SQLException {
                return con.prepareStatement(
                        "SELECT `id` FROM " + tableName);
            }
        }, RowMapping.INT_ROW_MAPPING);
    }

    @Nonnull
    @Override
    public List<String> values() {
        return queryForList(new StatementCreator() {
                    @Nonnull
                    @Override
                    public PreparedStatement createStatement(@Nonnull Connection con) throws SQLException {
                        return con.prepareStatement(
                                "SELECT `value` FROM " + tableName);
                    }
                }, RowMapping.STR_ROW_MAPPING);
    }

    @Nonnull
    @Override
    public Map<Integer, String> entries() {
        List<Pair<Integer, String>> ls = queryForList(new StatementCreator() {
            @Nonnull
            @Override
            public PreparedStatement createStatement(@Nonnull Connection con) throws SQLException {
                return con.prepareStatement(
                        "SELECT `id`, `value` FROM " + tableName);
            }
        }, PAIR_MAPPING);
        if (ls.size() == 0)
            return Collections.emptyMap();

        HashMap<Integer, String> map = new HashMap<>(ls.size() * 4 / 3 + 1);
        for (Pair<Integer, String> p : ls)
            map.put(p.first, p.second);
        return map;
    }

    @Override
    public boolean update(final int key, @Nonnull final String value) {
        return execute(new StatementCreator() {
            @Nonnull
            @Override
            public PreparedStatement createStatement(@Nonnull Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "UPDATE " + tableName + " SET `value` = ? where id=" + key);
                ps.setString(1, value);
                return ps;
            }
        });
    }

    protected boolean doPut(final int key, @Nonnull final String value) {
        insert(new StatementCreator() {
            @Nonnull
            @Override
            public PreparedStatement createStatement(@Nonnull Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO " + tableName + "(`id`, `value`)VALUES(?,?)");
                ps.setInt(1, key);
                ps.setString(2, value);
                return ps;
            }
        }, null, null);
        return true;
    }


    @Override
    public boolean putAll(@Nonnull final Map<Integer, String> map) {
        batchInsert(new StatementCreator() {
            @Nonnull
            @Override
            public PreparedStatement createStatement(@Nonnull Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO " + tableName + "(`id`, `value`)VALUES(?,?)");
                for (Map.Entry<Integer, String> e : map.entrySet()) {
                    ps.setInt(1, e.getKey());
                    ps.setString(2, e.getValue());
                    ps.addBatch();
                }
                return ps;
            }
        }, null, null);

        return true;
    }

    @Nullable
    @Override
    public String remove(final int key, boolean returnVal) throws SQLException {
        String ret = returnVal ? get(key) : null;
        boolean ok = execute(new StatementCreator() {
            @Nonnull
            @Override
            public PreparedStatement createStatement(@Nonnull Connection con) throws SQLException {
                return con.prepareStatement(
                        "DELETE FROM " + tableName + "where id=" + key);
            }
        });
        if (!ok)
            throw new SQLException("failed from delete value, id=" + key);
        return ret;
    }

}
