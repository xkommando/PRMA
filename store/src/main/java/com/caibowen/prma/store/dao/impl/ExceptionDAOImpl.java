package com.caibowen.prma.store.dao.impl;

import com.caibowen.gplume.jdbc.JdbcSupport;
import com.caibowen.gplume.jdbc.StatementCreator;
import com.caibowen.gplume.jdbc.mapper.RowMapping;
import com.caibowen.gplume.misc.Bytes;
import com.caibowen.gplume.misc.Hashing;
import com.caibowen.prma.api.model.ExceptionVO;
import com.caibowen.prma.store.ExceptionDO;
import com.caibowen.prma.store.dao.ExceptionDAO;
import com.caibowen.prma.store.dao.Int4DAO;
import com.caibowen.prma.store.dao.StackTraceDAO;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author BowenCai
 * @since 23-10-2014.
 */
public class ExceptionDAOImpl extends JdbcSupport implements ExceptionDAO {

    @Inject Int4DAO<String> exceptNameDAO;
    @Inject Int4DAO<String> exceptMsgDAO;

    @Inject StackTraceDAO stackTraceDAO;

    private static <V> void
    persist(Int4DAO<V> dao, int hash, V obj) {
        if ( !dao.putIfAbsent(hash, obj))
            throw new RuntimeException(dao.toString()
                    + "  could not save value["
                    + (null == obj ? "null" : obj.toString()) + " with id " + hash);
    }

    public static final int[] EMPTY_INTS = {};

    @Override
    public boolean insertIfAbsent(final long eventId, final List<ExceptionVO> tpox) throws Exception {
        ArrayList<ExceptionDO> dos = new ArrayList<>(tpox.size());
        for (ExceptionVO d : tpox) {
            if (! hasKey(d.id))
                dos.add(getDO(d));
        }
        if (dos.size() > 0)
            return insertAll(eventId, dos);
        else
            return true;
    }


    public boolean insertAll(final long eventID, final List<ExceptionDO> vols) {

        batchInsert(new StatementCreator() {
            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO `exception`(`id`" +
                                ",`except_name`,`except_msg`,`stack_traces`)" +
                                "VALUES (?,?,?,?)");

                for (ExceptionDO vo : vols) {
                    ps.setLong(1, vo.id);
                    ps.setInt(2, vo.exceptName);
                    ps.setInt(3, vo.exceptMsg);

                    byte[] buf = Bytes.int2byte(vo.stackTraces);
                    ps.setBytes(4, buf);

                    ps.addBatch();
                }
                return ps;
            }
        }, null, null);

        batchInsert(new StatementCreator() {
            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO `j_event_exception`(`seq`,`event_id`,`except_id`)VALUES(?,?,?)");

                for (int i = 0; i < vols.size(); i++) {
                    ps.setInt(1, i);
                    ps.setLong(2, eventID);
                    ps.setLong(3, vols.get(i).id);
                    ps.addBatch();
                }
//                throw new NoSuchElementException("weird exception to inside jdbc operation");
                return ps;
            }
        }, null, null);

        return true;
    }

    ExceptionDO getDO(@Nonnull ExceptionVO tpox) {
        ExceptionDO vo = new ExceptionDO();

        String _thwName = tpox.getClass().getName();
        final int thwID = _thwName.hashCode();
        persist(exceptNameDAO, thwID, _thwName);
        vo.exceptName = thwID;

        String _msg = tpox.exceptionMessage;
        final int msgID = _msg.hashCode();
        persist(exceptMsgDAO, msgID, _msg);
        vo.exceptMsg = msgID;

        StackTraceElement[] stps = tpox.stackTraces;
        int[] buf;
        if (stps != null && stps.length > 0) {
            buf = new int[stps.length];
            for (int i = 0; i < stps.length; i++) {
                StackTraceElement st = stps[i];
                int id = st.hashCode();
                if (!stackTraceDAO.putIfAbsent(id, st))
                    throw new RuntimeException("could not save stack trace "
                            + st.toString());

                buf[i] = id;
            }
        } else
            buf = EMPTY_INTS;

        vo.id = tpox.id;
        vo.stackTraces = buf;
        return vo;
    }

    ExceptionDO getDO(@Nonnull Throwable tpox) {

        ExceptionDO vo = new ExceptionDO();

        String _thwName = tpox.getClass().getName();
        final int thwID = _thwName.hashCode();
        persist(exceptNameDAO, thwID, _thwName);
        vo.exceptName = thwID;

        String _msg = tpox.getMessage();
        final int msgID = _msg.hashCode();
        long expID =  (long)thwID << 32 | msgID & 0xFFFFFFFFL;
        persist(exceptMsgDAO, msgID, _msg);
        vo.exceptMsg = msgID;

        StackTraceElement[] stps = tpox.getStackTrace();
        int[] buf;
        if (stps != null && stps.length > 0) {
            buf = new int[stps.length];
            for (int i = 0; i < stps.length; i++) {
                StackTraceElement st = stps[i];
                int id = st.hashCode();
                if (!stackTraceDAO.putIfAbsent(id, st))
                    throw new RuntimeException("could not save stack trace "
                            + st.toString());

                buf[i] = id;
                expID = Hashing.hash128To64(expID, Hashing.twang_mix64((long) id));
            }
        } else
            buf = EMPTY_INTS;

        vo.id = expID;
        vo.stackTraces = buf;
        return vo;
    }

    @Override
    public boolean hasKey(final long hash) {
        return queryForObject(new StatementCreator() {
            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "SELECT count(1) FROM `exception` WHERE id = " + hash);
                return ps;
            }
        }, RowMapping.BOOLEAN_ROW_MAPPING);
    }
}
