package com.caibowen.prma.store.dao.impl;

import com.caibowen.gplume.common.Pair;
import com.caibowen.prma.jdbc.JdbcAux;
import com.caibowen.prma.jdbc.callback.StatementCreator;
import com.caibowen.prma.jdbc.mapper.RowMapping;
import com.caibowen.prma.store.dao.StackTraceDAO;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author BowenCai
 * @since 24-10-2014.
 */
public class StackTraceDAOImpl extends JdbcAux implements StackTraceDAO {

    public static final RowMapping<StackTraceElement> ST_MAPPING = new RowMapping<StackTraceElement>() {
        @Override
        public StackTraceElement extract(ResultSet rs) throws SQLException {
            String file = rs.getString(1);
            String klass = rs.getString(2);
            String func = rs.getString(3);
            int line = rs.getInt(4);

            return new StackTraceElement(klass, func, file, line);
        }
    };

    public static final RowMapping<Pair<Integer, StackTraceElement>> PAIR_MAPPING = new RowMapping<Pair<Integer, StackTraceElement>>() {
        @Override
        public Pair<Integer, StackTraceElement> extract(ResultSet rs) throws SQLException {
            int id = rs.getInt(5);
            return new Pair<>(id, ST_MAPPING.extract(rs));
        }
    };

    @Override
    public boolean hasKey(final int key) {
        return 0 < queryForObject(new StatementCreator() {
            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "SELECT count(1) FROM `stack_trace` WHERE id = " + key);

                return ps;
            }
        }, RowMapping.INT_ROW_MAPPING);
    }

    @Override
    public boolean hasVal(@Nonnull final StackTraceElement val) {
        return hasKey(val.hashCode());
    }

    @Nullable
    @Override
    public StackTraceElement get(final int key) {
        return queryForObject(new StatementCreator() {
            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "SELECT `file`,`class`,`function`,`line` FROM `stack_trace` WHERE id=" + key);
                return ps;
            }
        }, ST_MAPPING);
    }

    @Nonnull
    @Override
    public List<Integer> keys() {
        return queryForList(new StatementCreator() {
            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "SELECT `id` FROM `stack_trace`");
                return ps;
            }
        }, RowMapping.INT_ROW_MAPPING);
    }

    @Nonnull
    @Override
    public List<StackTraceElement> values() {
        return queryForList(new StatementCreator() {
            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "SELECT `file`,`class`,`function`,`line` FROM `stack_trace`");
                return ps;
            }
        }, ST_MAPPING);
    }

    @Nonnull
    @Override
    public Map<Integer, StackTraceElement> entries() {
        List<Pair<Integer, StackTraceElement>> ls = queryForList(new StatementCreator() {
            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "SELECT `file`,`class`,`function`,`line`,`id` FROM `stack_trace`");
                return ps;
            }
        }, PAIR_MAPPING);
        if (ls.size() == 0)
            return Collections.emptyMap();

        HashMap<Integer, StackTraceElement> map = new HashMap<>(ls.size() * 4 / 3 + 1);
        for (Pair<Integer, StackTraceElement> p : ls)
            map.put(p.first, p.second);
        return map;
    }

    @Nonnull
    @Override
    public boolean putIfAbsent(final int key, @Nonnull final StackTraceElement value) {
        return hasKey(key) || execute(new StatementCreator() {
            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO `stack_trace`(`id`,`file`,`class`,`function`,`line`)VALUES(?,?,?,?,?)");
                ps.setLong(1, value.hashCode());
                ps.setString(2, value.getFileName());
                ps.setString(3, value.getClassName());
                ps.setString(4, value.getMethodName());
                ps.setInt(5, value.getLineNumber());
                return ps;
            }
        });
    }

    @Nonnull
    @Override
    public boolean putAll(final Map<Integer, StackTraceElement> map) {

        batchInsert(new StatementCreator() {
            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO `stack_trace`(`id`,`file`,`class`,`function`,`line`)VALUES(?,?,?,?)");

                for (Map.Entry<Integer, StackTraceElement> e : map.entrySet()) {
                    StackTraceElement value = e.getValue();
                    ps.setLong(1, value.hashCode());
                    ps.setString(2, value.getFileName());
                    ps.setString(3, value.getClassName());
                    ps.setString(4, value.getMethodName());
                    ps.setInt(5, value.getLineNumber());
                    ps.addBatch();
                }
                return ps;
            }
        }, null, null);

        return true;
    }

    @Nonnull
    @Override
    public boolean update(final int key, @Nonnull final StackTraceElement value) {
        throw new NoSuchMethodError("not implemented yet");
//        return execute(new StatementCreator() {
//            @Override
//            public PreparedStatement createStatement(Connection con) throws SQLException {
//                PreparedStatement ps = con.prepareStatement(
//                        "UPDATE `stack_trace` SET `value` = ? where id=" + key);
//                ps.setString(1, value);
//                return ps;
//            }
//        });
    }

    @Nullable
    @Override
    public StackTraceElement remove(final int key, boolean returnVal) throws SQLException {
        StackTraceElement ret = returnVal ? get(key) : null;
        boolean ok = execute(new StatementCreator() {
            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM `stack_trace`where id=" + key);
                return ps;
            }
        });
        if (!ok)
            throw new SQLException("failed to delete value, id=" + key);
        return ret;
    }
}
