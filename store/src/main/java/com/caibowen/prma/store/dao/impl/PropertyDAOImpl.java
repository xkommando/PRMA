package com.caibowen.prma.store.dao.impl;

import com.caibowen.gplume.jdbc.JdbcSupport;
import com.caibowen.gplume.jdbc.StatementCreator;
import com.caibowen.gplume.jdbc.mapper.RowMapping;
import com.caibowen.prma.store.dao.PropertyDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

/**
 * @author BowenCai
 * @since 24-10-2014.
 */
public class PropertyDAOImpl extends JdbcSupport implements PropertyDAO {

    @Override
    public boolean insertIfAbsent(final long eventId, final Map<String, String> prop) {

        Iterator iter = prop.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry pairs = (Map.Entry)iter.next();
            if (hasKey(pairs.getKey().hashCode()))
                iter.remove();
        }
        if (prop.isEmpty())
            return true;
        else
            return insertAll(eventId, prop);
    }

    @Override
    public boolean insertAll(final long eventId, final Map<String, String> prop) {

        batchInsert(new StatementCreator() {
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

    @Override
    public boolean hasKey(final int hash) {
        return queryForObject(new StatementCreator() {
            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "SELECT count(1) FROM `property` WHERE id = " + hash);
                return ps;
            }
        }, RowMapping.BOOLEAN_ROW_MAPPING);
    }
}
