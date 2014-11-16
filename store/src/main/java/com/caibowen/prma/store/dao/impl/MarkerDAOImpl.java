package com.caibowen.prma.store.dao.impl;

import com.caibowen.gplume.jdbc.StatementCreator;
import com.caibowen.gplume.jdbc.mapper.RowMapping;
import com.caibowen.prma.core.StringLoader;
import com.caibowen.prma.store.dao.MarkerDAO;

import javax.annotation.Nonnull;
import javax.inject.Inject;
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

    @Inject
    StringLoader sqls;

    public MarkerDAOImpl() {
        super("`marker_name`");
    }

    @Override
    public Set<String> getByEvent(final long eventId) {
        return new TreeSet<>(queryForList(new StatementCreator() {
            @Nonnull
            @Override
            public PreparedStatement createStatement(@Nonnull Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(sqls.get("MarkerDAO.getByEvent"));
                ps.setLong(1, eventId);
                return ps;
            }
        }, RowMapping.STR_ROW_MAPPING));
    }

    @Override
    public boolean insertIfAbsent(long eventId, Set<String> markers) {
        Set<String> nms = new TreeSet<>();
        for (String s : markers)
            if (!hasKey(s.hashCode()))
                nms.add(s);
        if (nms.size() > 0)
            putMarker(nms);
        putRelation(eventId, markers);
        return true;
    }

    @Override
    public boolean insertAll(final long eventId, final Set<String> markers) {
        putMarker(markers);
        putRelation(eventId, markers);
        return true;
    }

    private void putMarker(final Set<String> markers) {
        batchInsert(new StatementCreator() {
            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        sqls.get("MarkerDAO.putMarker"));
                for (String e : markers) {
                    ps.setInt(1, e.hashCode());
                    ps.setString(2, e);
                    ps.addBatch();
                }
                return ps;
            }
        }, null, null);
    }

    private void putRelation(final long eventId, final Set<String> markers) {
        batchInsert(new StatementCreator() {
            @Nonnull
            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        sqls.get("MarkerDAO.putRelation"));
                for (String e : markers) {
                    ps.setInt(1, e.hashCode());
                    ps.setLong(2, eventId);
                    ps.addBatch();
                }
                return ps;
            }
        }, null, null);
    }
}
