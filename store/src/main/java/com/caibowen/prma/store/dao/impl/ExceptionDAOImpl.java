package com.caibowen.prma.store.dao.impl;

import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import com.caibowen.gplume.jdbc.JdbcSupport;
import com.caibowen.gplume.jdbc.StatementCreator;
import com.caibowen.gplume.misc.Bytes;
import com.caibowen.gplume.misc.Hashing;
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
    public boolean insert(final long eventID, IThrowableProxy tpox) {

        final ArrayList<ExceptionDO> vols = new ArrayList<>(16);
        IThrowableProxy px = tpox;
        while (px != null) {
            ExceptionDO vo = getDO(px);
            vols.add(vo);
            px = px.getCause();
        }

        batchInsert(new StatementCreator() {
            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                "INSERT INTO `exception`(`id`,`time_created`" +
                        ",`except_name`,`except_msg`,`stack_traces`)" +
                        "VALUES (?,?,?,?,?)");

                for (ExceptionDO vo : vols) {
                    ps.setLong(1, vo.id);
                    ps.setLong(2, vo.timeCreated);
                    ps.setInt(3, vo.exceptName);
                    ps.setInt(4, vo.exceptMsg);

                    byte[] buf = Bytes.int2byte(vo.stackTraces);
                    ps.setBytes(5, buf);

                    ps.addBatch();
                }
                return ps;
            }
        }, null, null);

        insert(new StatementCreator() {
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
//                throw new NoSuchElementException("weird exception from inside jdbc operation");
                return ps;
            }
        }, null, null);

        return true;
    }

    //    if (event.getThrowableProxy() != null)
    ExceptionDO getDO(@Nonnull IThrowableProxy tpox) {

        ExceptionDO vo = new ExceptionDO();
        vo.timeCreated = System.currentTimeMillis();

        String _thwName = tpox.getClassName();
        final int thwID = _thwName.hashCode();
        persist(exceptNameDAO, thwID, _thwName);
        vo.exceptName = thwID;

        String _msg = tpox.getMessage();
        final int msgID = _msg.hashCode();
        long expID =  (long)thwID << 32 | msgID & 0xFFFFFFFFL;
        persist(exceptMsgDAO, msgID, _msg);
        vo.exceptMsg = msgID;

        StackTraceElementProxy[] stps = tpox.getStackTraceElementProxyArray();
        int[] buf;
        if (stps != null && stps.length > 0) {
            buf = new int[stps.length];
            for (int i = 0; i < stps.length; i++) {
                StackTraceElement st = stps[i].getStackTraceElement();
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

}
