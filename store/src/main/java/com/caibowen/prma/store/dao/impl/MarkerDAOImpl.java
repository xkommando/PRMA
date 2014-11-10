package com.caibowen.prma.store.dao.impl;

import com.caibowen.gplume.jdbc.StatementCreator;
import com.caibowen.prma.store.dao.MarkerDAO;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author BowenCai
 * @since 6-11-2014.
 */
public class MarkerDAOImpl extends StrDAOImple implements MarkerDAO {

    private static final long serialVersionUID = 7519117476159010044L;

    public MarkerDAOImpl() {
        super("`marker_name`");
    }

    @Override
    public boolean insertIfAbsent(long eventId, Set<String> markers) {
        Set<String> nms = new TreeSet<>();
        for (String s : markers)
            if (!hasKey(s.hashCode()))
                nms.add(s);

        return nms.size() <= 0 || insertAll(eventId, nms);
    }

    @Override
    public boolean insertAll(final long eventId, final Set<String> markers) {

        batchInsert(new StatementCreator() {
            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO `marker_name`(`id`, `value`)VALUES(?,?)");
                for (String e : markers) {
                    ps.setInt(1, e.hashCode());
                    ps.setString(2, e);
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
                        "INSERT INTO `j_event_marker`(`marker_id`, `event_id`)VALUES(?,?)");
                for (String e : markers) {
                    ps.setInt(1, e.hashCode());
                    ps.setLong(2, eventId);
                    ps.addBatch();
                }
                return ps;
            }
        }, null, null);

        return true;
    }
}
