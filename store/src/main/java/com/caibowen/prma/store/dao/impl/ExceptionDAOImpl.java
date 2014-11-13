package com.caibowen.prma.store.dao.impl;

import com.caibowen.gplume.jdbc.JdbcSupport;
import com.caibowen.gplume.jdbc.StatementCreator;
import com.caibowen.gplume.jdbc.mapper.RowMapping;
import com.caibowen.gplume.misc.Bytes;
import com.caibowen.gplume.misc.Hashing;
import com.caibowen.prma.api.model.ExceptionVO;
import com.caibowen.prma.core.filter.basic.PartialStrFilter;
import com.caibowen.prma.core.filter.basic.StrFilter;
import com.caibowen.prma.store.ExceptionDO;
import com.caibowen.prma.store.dao.ExceptionDAO;
import com.caibowen.prma.store.dao.Int4DAO;
import com.caibowen.prma.store.dao.StackTraceDAO;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

/**
 * @author BowenCai
 * @since 23-10-2014.
 */
public class ExceptionDAOImpl extends JdbcSupport implements ExceptionDAO {

    @Inject
    StrFilter exceptionFilter; // actual -> partial string filter

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

    @Override
    public boolean insertIfAbsent(final long eventId, final List<ExceptionVO> tpox) throws Exception {
        Set<ExceptionDO> dos = new TreeSet<ExceptionDO>(new Comparator<ExceptionDO>() {
            @Override
            public int compare(ExceptionDO o1, ExceptionDO o2) {
                return (int)(o1.id - o2.id);
            }
        });

        for (ExceptionVO d : tpox) {
            if (! hasKey(d.id)
                    && exceptionFilter.accept(d.exceptionName) != 1)
                dos.add(getDO(d));
        }

        if (dos.size() > 0)
            return insertAll(eventId, new ArrayList<>(dos));
        else
            return true;
    }


    /**
     *  not filtered!
     *
     * @param eventID
     * @param vols
     * @return
     */
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

                    if (vo.exceptMsg != null)
                        ps.setInt(3, vo.exceptMsg);
                    else
                        ps.setNull(3, Types.INTEGER);

                    if (vo.stackTraces != null && vo.stackTraces.length > 0) {
                        byte[] buf = Bytes.int2byte(vo.stackTraces);
                        ps.setBytes(4, buf);
                    } else
                        ps.setNull(4, Types.BINARY);

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
//                throw new NoSuchElementException("TEST: weird exception to inside jdbc operation");
                return ps;
            }
        }, null, null);

        return true;
    }

    ExceptionDO getDO(@Nonnull ExceptionVO tpox) {
        ExceptionDO vo = new ExceptionDO();
        vo.id = tpox.id;

        String _thwName = tpox.getClass().getName();
        final int thwID = _thwName.hashCode();
        persist(exceptNameDAO, thwID, _thwName);
        vo.exceptName = thwID;

        String _msg = tpox.exceptionMessage;
        if (_msg != null) {
            final int msgID = _msg.hashCode();
            persist(exceptMsgDAO, msgID, _msg);
            vo.exceptMsg = msgID;
        }

        StackTraceElement[] stps = tpox.stackTraces;
        int[] buf = null;
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
        }
        vo.stackTraces = buf;
        return vo;
    }

    @Override
    public boolean hasKey(final long hash) {
        return queryForObject(new StatementCreator() {
            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
                return con.prepareStatement(
                        "SELECT count(1) FROM `exception` WHERE id = " + hash);
            }
        }, RowMapping.BOOLEAN_ROW_MAPPING);
    }


    public void setExceptionFilter(StrFilter exceptionFilter) {
        this.exceptionFilter = exceptionFilter;
    }

    public void setExceptNameDAO(Int4DAO<String> exceptNameDAO) {
        this.exceptNameDAO = exceptNameDAO;
    }

    public void setExceptMsgDAO(Int4DAO<String> exceptMsgDAO) {
        this.exceptMsgDAO = exceptMsgDAO;
    }

    public void setStackTraceDAO(StackTraceDAO stackTraceDAO) {
        this.stackTraceDAO = stackTraceDAO;
    }
}
