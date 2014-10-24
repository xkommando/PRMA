package com.caibowen.prma.store.dao.impl;

import com.caibowen.prma.jdbc.JdbcAux;
import com.caibowen.prma.jdbc.StatementCreator;
import com.caibowen.prma.store.dao.PropertyDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author BowenCai
 * @since 24-10-2014.
 */
public class PropertyDAOImpl extends JdbcAux implements PropertyDAO {

    @Override
    public boolean insert(final long eventId, final Map<String, String> prop) {
        batchInsert(new StatementCreator() {
            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO `property`(`id`, `key`, `value`)VALUES (?,?,?)");
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
