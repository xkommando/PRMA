package com.caibowen.prma.store.rdb.dao.impl;

import com.caibowen.gplume.jdbc.JdbcSupport;
import com.caibowen.gplume.jdbc.StatementCreator;
import com.caibowen.gplume.jdbc.mapper.RowMapping;
import com.caibowen.gplume.misc.Bytes;
import com.caibowen.prma.api.model.ExceptionVO;
import com.caibowen.prma.core.StrLoader;
import com.caibowen.prma.core.filter.StrFilter;
import com.caibowen.prma.store.rdb.ExceptionDO;
import com.caibowen.prma.store.rdb.dao.ExceptionDAO;
import com.caibowen.prma.store.rdb.dao.Int4DAO;
import com.caibowen.prma.store.rdb.dao.StackTraceDAO;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.sql.*;
import java.util.*;

/**
 * @author BowenCai
 * @since 23-10-2014.
 */
public class ExceptionDAOImpl extends JdbcSupport implements ExceptionDAO {

    @Inject
    StrFilter exceptionFilter; // actual: partial string filter

    @Inject
    StrFilter stackTraceFilter; // class name based filtering

    @Inject Int4DAO<String> exceptNameDAO;
    @Inject Int4DAO<String> exceptMsgDAO;
    @Inject StackTraceDAO stackTraceDAO;

    @Inject
    final StrLoader sqls;

    public ExceptionDAOImpl(StrLoader loader) {
        this.sqls = loader;
    }

    RowMapping<ExceptionVO> VO_MAPPING = new RowMapping<ExceptionVO>() {

        public ExceptionVO extract(@Nonnull ResultSet rs) throws SQLException {
            ExceptionVO vo = new ExceptionVO();
            vo.id = rs.getLong(1);
            vo.exceptionName = exceptNameDAO.get(rs.getInt(2));
            vo.exceptionMessage = exceptMsgDAO.get(rs.getInt(3));

            byte[] bsts = rs.getBytes(4);
            if (bsts != null && bsts.length > 0) {
                int[] stids = Bytes.bytes2ints(bsts);
                StackTraceElement[] sts = new StackTraceElement[stids.length];
                for (int i = 0; i < stids.length; ++i) {
                    sts[i] = stackTraceDAO.get(stids[i]);
                }
                vo.stackTraces = sts;
            }
            return vo;
        }

    };

    @Override
    public List<ExceptionVO> getByEvent(final long eventId) {
        return queryForList(new StatementCreator() {
            @Nonnull
            @Override
            public PreparedStatement createStatement(@Nonnull Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        sqls.get("ExceptionDAO.getByEvent"));
                ps.setLong(1, eventId);
                return ps;
            }
        }, VO_MAPPING);
    }

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
        List vols = new LinkedList(tpox);
        Iterator<ExceptionVO> iter = vols.iterator();
        while (iter.hasNext()) {
            ExceptionVO vo = iter.next();
            if (exceptionFilter.accept(vo.exceptionName) == 1) {
                iter.remove();
                continue;
            }
            if (! hasKey(vo.id))
                dos.add(getDO(vo));
        }

        putRelationVO(eventId, vols);
        // reuse list
        if (dos.size() > 0) {
            vols.clear();
            vols.addAll(dos);
            putExcept(vols);
        }
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
        putExcept(vols);
        putRelationDO(eventID, vols);
        return true;
    }

    private void putExcept(final List<ExceptionDO> vols) {
        batchInsert(new StatementCreator() {
            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        sqls.get("ExceptionDAO.putExcept"));

                for (ExceptionDO expDo : vols) {
                    ps.setLong(1, expDo.id);
                    ps.setInt(2, expDo.exceptName);

                    if (expDo.exceptMsg != null)
                        ps.setInt(3, expDo.exceptMsg);
                    else
                        ps.setNull(3, Types.INTEGER);

                    if (expDo.stackTraces != null && expDo.stackTraces.length > 0) {
                        byte[] buf = Bytes.ints2bytes(expDo.stackTraces);
                        ps.setBytes(4, buf);
                    } else
                        ps.setNull(4, Types.BINARY);

                    ps.addBatch();
                }
                return ps;
            }
        }, null, null);
    }

    private void putRelationVO(final long eventID, final List<ExceptionVO> vols) {
        batchInsert(new StatementCreator() {
            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        sqls.get("ExceptionDAO.putRelation"));

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
    }
    private void putRelationDO(final long eventID, final List<ExceptionDO> vols) {
        batchInsert(new StatementCreator() {
            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        sqls.get("ExceptionDAO.putRelation"));
                for (int i = 0; i < vols.size(); i++) {
                    ps.setInt(1, i);
                    ps.setLong(2, eventID);
                    ps.setLong(3, vols.get(i).id);
                    ps.addBatch();
                }
                return ps;
            }
        }, null, null);
    }


    ExceptionDO getDO(@Nonnull ExceptionVO tpox) {
        ExceptionDO vo = new ExceptionDO();
        vo.id = tpox.id;

        String _thwName = tpox.exceptionName;
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
        if (stps == null || stps.length == 0)
            return vo;

        ArrayList<Integer> buf = new ArrayList<>(stps.length);
        for (int i = 0; i < stps.length; i++) {
            StackTraceElement st = stps[i];
            if (stackTraceFilter.accept(st.getClassName()) == 1)
                continue;

            int id = st.hashCode();
            if (!stackTraceDAO.putIfAbsent(id, st))
                throw new RuntimeException("could not save stack trace "
                        + st.toString());

            buf.add(id);
        }
        if (buf.size() == 0)
            return vo;

        int[] ids = new int[buf.size()];
        Iterator<Integer> it = buf.iterator();
        int i = 0;
        while (it.hasNext())
            ids[i++] = it.next();
        vo.stackTraces = ids;
        return vo;
    }

    @Override
    public boolean hasKey(final long hash) {
        return queryForObject(new StatementCreator() {
            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "SELECT count(1) FROM `exception` WHERE id =?");
                ps.setLong(1, hash);
                return ps;
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
