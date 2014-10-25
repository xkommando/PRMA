package com.caibowen.prma.store.dao.impl;

import com.caibowen.gplume.common.Pair;
import com.caibowen.prma.jdbc.JdbcAux;
import com.caibowen.prma.jdbc.StatementCreator;
import com.caibowen.prma.jdbc.mapper.RowMapping;
import com.caibowen.prma.store.dao.Int4DAO;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author BowenCai
 * @since 23-10-2014.
 */
public class StrDAOImple extends JdbcAux implements Int4DAO<String> {

    public static final RowMapping<Pair<Integer, String> > PAIR_MAPPING = new RowMapping<Pair<Integer, String>>() {
        @Override
        public Pair<Integer, String> extract(ResultSet rs) throws SQLException {
            return new Pair<>(rs.getInt(1), rs.getString(2));
        }
    };

    private final String tableName;

    public StrDAOImple(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public boolean hasKey(final int key) {
        return queryForObject(new StatementCreator() {
            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "SELECT count(1) FROM " + tableName + " WHERE id = " + key);

                return ps;
            }
        }, RowMapping.BOOLEAN_ROW_MAPPING);
    }

    @Override
    public boolean hasVal(@Nonnull final String val) {
        return queryForObject(new StatementCreator() {
            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
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
            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "SELECT `value` FROM " + tableName + " WHERE id=" + key);
                return ps;
            }
        }, RowMapping.STR_ROW_MAPPING);
    }

    @Nonnull
    @Override
    public List<Integer> keys() {
        return queryForList(new StatementCreator() {
            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "SELECT `id` FROM " + tableName);
                return ps;
            }
        }, RowMapping.INT_ROW_MAPPING);
    }

    @Nonnull
    @Override
    public List<String> values() {
        return queryForList(new StatementCreator() {
                    @Override
                    public PreparedStatement createStatement(Connection con) throws SQLException {
                        PreparedStatement ps = con.prepareStatement(
                                "SELECT `value` FROM " + tableName);
                        return ps;
                    }
                }, RowMapping.STR_ROW_MAPPING);
    }

    @Nonnull
    @Override
    public List<Pair<Integer, String>> entries() {
        return queryForList(new StatementCreator() {
            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "SELECT `value` FROM " + tableName);
                return ps;
            }
        }, PAIR_MAPPING);
    }

    @Nonnull
    @Override
    public boolean putIfAbsent(final int key, @Nonnull final String value) {
        return hasKey(key) || execute(new StatementCreator() {
            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO " + tableName + "(`id`, `value`)VALUES(?,?)");
                ps.setInt(1, key);
                ps.setString(2, value);
                return ps;
            }
        });
    }

    @Nonnull
    @Override
    public boolean putAll(final Map<Integer, String> map) {

        batchInsert(new StatementCreator() {
            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
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

    @Nonnull
    @Override
    public boolean update(final int key, @Nonnull final String value) {
        return execute(new StatementCreator() {
            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                "UPDATE " + tableName + " SET `value` = ? where id=" + key);
                ps.setString(1, value);
                return ps;
            }
        });
    }

    @Nullable
    @Override
    public String remove(final int key, boolean returnVal) throws SQLException {
        String ret = returnVal ? get(key) : null;
        boolean ok = execute(new StatementCreator() {
            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM " + tableName + "where id=" + key);
                return ps;
            }
        });
        if (!ok)
            throw new SQLException("failed to delete value, id=" + key);
        return ret;
    }

    public String getTableName() {
        return tableName;
    }
}
