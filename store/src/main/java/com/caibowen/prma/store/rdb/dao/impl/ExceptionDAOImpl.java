//package com.caibowen.prma.store.rdb.dao.impl;
//
//import com.caibowen.gplume.jdbc.JdbcSupport;
//import com.caibowen.gplume.jdbc.StatementCreator;
//import com.caibowen.gplume.jdbc.mapper.RowMapping;
//import com.caibowen.gplume.misc.Bytes;
//import com.caibowen.prma.api.model.ExceptionVO;
//import com.caibowen.prma.core.StrLoader;
//import com.caibowen.prma.core.filter.StrFilter;
//import com.caibowen.prma.store.rdb.ExceptionDO;
//import com.caibowen.prma.store.rdb.dao.ExceptionDAO;
//import com.caibowen.prma.store.rdb.dao.KVStore;
//import com.caibowen.prma.store.rdb.dao.StackTraceDAO;
//import scala.collection.JavaConversions;
//import scala.collection.immutable.List;
//import scala.Option;
//import javax.annotation.Nonnull;
//import javax.inject.Inject;
//import java.sql.*;
//import java.util.*;
//
///**
// * @author BowenCai
// * @since 23-10-2014.
// */
//public class ExceptionDAOImpl extends JdbcSupport implements ExceptionDAO {
//
//    @Inject
//    StrFilter exceptionFilter; // actual: partial string filter
//
//    @Inject
//    StrFilter stackTraceFilter; // class name based filtering
//
//    @Inject KVStore<String> exceptNameDAO;
//    @Inject KVStore<String> exceptMsgDAO;
//    @Inject StackTraceDAO stackTraceDAO;
//
//    @Inject
//    final StrLoader sqls;
//
//    public ExceptionDAOImpl(StrLoader loader) {
//        this.sqls = loader;
//    }
//
////    RowMapping<ExceptionVO> VO_MAPPING = new RowMapping<ExceptionVO>() {
////
////        public ExceptionVO extract(@Nonnull ResultSet rs) throws SQLException {
////            ExceptionVO vo = new ExceptionVO();
////            vo.id = rs.getLong(1);
////            vo.exceptionName = exceptNameDAO.get(rs.getInt(2));
////            vo.exceptionMessage = exceptMsgDAO.get(rs.getInt(3));
////
////            byte[] bsts = rs.getBytes(4);
////            if (bsts != null && bsts.length > 0) {
////                int[] stids = Bytes.bytes2ints(bsts);
////                StackTraceElement[] sts = new StackTraceElement[stids.length];
////                for (int i = 0; i < stids.length; ++i) {
////                    sts[i] = stackTraceDAO.get(stids[i]);
////                }
////                vo.stackTraces = sts;
////            }
////            return vo;
////        }
////
////    };
//
////    @Override
////    public List<ExceptionVO> getByEvent(final long eventId) {
////        return queryForList(new StatementCreator() {
////            @Nonnull
////            @Override
////            public PreparedStatement createStatement(@Nonnull Connection con) throws SQLException {
////                PreparedStatement ps = con.prepareStatement(
////                        sqls.get("ExceptionDAO.getByEvent"));
////                ps.setLong(1, eventId);
////                return ps;
////            }
////        }, VO_MAPPING);
////    }
//
//    private static <V> void
//    persist(KVStore<V> dao, int hash, V obj) {
//        if ( !dao.putIfAbsent(hash, obj))
//            throw new RuntimeException(dao.toString()
//                    + "  could not save value["
//                    + (null == obj ? "null" : obj.toString()) + " with id " + hash);
//    }
//
//    @Override
//    public boolean insertIfAbsent(final long eventId, final List<ExceptionVO> tpox) throws Exception {
//        TreeSet<ExceptionDO> dos = new TreeSet<ExceptionDO>(new Comparator<ExceptionDO>() {
//            @Override
//            public int compare(ExceptionDO o1, ExceptionDO o2) {
//                return (int)(o1.id - o2.id);
//            }
//        });
//        Iterable<ExceptionVO> vos = JavaConversions.asJavaIterable(tpox);
//        ArrayList newRelations = new ArrayList(tpox.length());
//        Iterator<ExceptionVO> iter = vos.iterator();
//        while (iter.hasNext()) {
//            ExceptionVO vo = iter.next();
//            if (exceptionFilter.accept(vo.getExceptionName()) == 1) {
//                continue;
//            }
//            if (! hasKey(vo.getId()))
//                dos.add(getDO(vo));
//            newRelations.add(vo);
//        }
//
//        putRelationVO(eventId, newRelations);
//        // reuse list
//        if (dos.size() > 0) {
//            newRelations.clear();
//            newRelations.addAll(dos);
//            putExcept(newRelations);
//        }
//        return true;
//    }
//
//
//    /**
//     *  not filtered!
//     *
//     * @param eventID
//     * @param vols
//     * @return
//     */
//    public boolean insertAll(final long eventID, final List<ExceptionDO> vols) {
//        putExcept(JavaConversions.asJavaIterable(vols));
//        putRelationDO(eventID, vols);
//        return true;
//    }
//
//    private void putExcept(final Iterable<ExceptionDO> vols) {
//        batchInsert(new StatementCreator() {
//            @Override
//            public PreparedStatement createStatement(Connection con) throws SQLException {
//                PreparedStatement ps = con.prepareStatement(
//                        sqls.get("ExceptionDAO.putExcept"));
//
//                for (ExceptionDO expDo : vols) {
//                    ps.setLong(1, expDo.id);
//                    ps.setInt(2, expDo.exceptName);
//
//                    if (expDo.exceptMsg != null)
//                        ps.setInt(3, expDo.exceptMsg);
//                    else
//                        ps.setNull(3, Types.INTEGER);
//
//                    if (expDo.stackTraces != null && expDo.stackTraces.length > 0) {
//                        byte[] buf = Bytes.ints2bytes(expDo.stackTraces);
//                        ps.setBytes(4, buf);
//                    } else
//                        ps.setNull(4, Types.BINARY);
//
//                    ps.addBatch();
//                }
//                return ps;
//            }
//        }, null, null);
//    }
//
//    private void putRelationVO(final long eventID, final Iterable<ExceptionVO> vols) {
//        batchInsert(new StatementCreator() {
//            @Override
//            public PreparedStatement createStatement(Connection con) throws SQLException {
//                PreparedStatement ps = con.prepareStatement(
//                        sqls.get("ExceptionDAO.putRelation"));
//                int i = 0;
//                for (ExceptionVO vo : vols) {
//                    ps.setInt(1, i);
//                    ps.setLong(2, eventID);
//                    ps.setLong(3, vo.getId());
//                    ps.addBatch();
//                    i++;
//                }
////                throw new NoSuchElementException("TEST: weird exception to inside jdbc operation");
//                return ps;
//            }
//        }, null, null);
//    }
//    private void putRelationDO(final long eventID, final List<ExceptionDO> vols) {
//        batchInsert(new StatementCreator() {
//            @Override
//            public PreparedStatement createStatement(Connection con) throws SQLException {
//                PreparedStatement ps = con.prepareStatement(
//                        sqls.get("ExceptionDAO.putRelation"));
//                for (int i = 0; i < vols.size(); i++) {
//                    ps.setInt(1, i);
//                    ps.setLong(2, eventID);
//                    ps.setLong(3, vols.apply(i).id);
//                    ps.addBatch();
//                }
//                return ps;
//            }
//        }, null, null);
//    }
//
//
//    ExceptionDO getDO(@Nonnull ExceptionVO tpox) {
//        ExceptionDO vo = new ExceptionDO();
//        vo.id = tpox.getId();
//
//        String _thwName = tpox.getExceptionName();
//        final int thwID = _thwName.hashCode();
//        persist(exceptNameDAO, thwID, _thwName);
//        vo.exceptName = thwID;
//
//        Option<String> _msg = tpox.getExceptionMessage();
//        if (_msg.isDefined()) {
//            final int msgID = _msg.get().hashCode();
//            persist(exceptMsgDAO, msgID, _msg.get());
//            vo.exceptMsg = msgID;
//        }
//
//        Option<List<StackTraceElement>> stps = tpox.getStackTraces();
//        if (stps.isEmpty())
//            return vo;
//        List<StackTraceElement> nspts = stps.get();
//        ArrayList<Integer> buf = new ArrayList<>(nspts.length());
//        for (int i = 0; i < nspts.length(); i++) {
//            StackTraceElement st = nspts.apply(i);
//            if (stackTraceFilter.accept(st.getClassName()) == 1)
//                continue;
//
//            int id = st.hashCode();
//            if (!stackTraceDAO.putIfAbsent(id, st))
//                throw new RuntimeException("could not save stack trace "
//                        + st.toString());
//
//            buf.add(id);
//        }
//        if (buf.size() == 0)
//            return vo;
//
//        int[] ids = new int[buf.size()];
//        Iterator<Integer> it = buf.iterator();
//        int i = 0;
//        while (it.hasNext())
//            ids[i++] = it.next();
//        vo.stackTraces = ids;
//        return vo;
//    }
//
//    @Override
//    public boolean hasKey(final long hash) {
//        return queryForObject(new StatementCreator() {
//            @Override
//            public PreparedStatement createStatement(Connection con) throws SQLException {
//                PreparedStatement ps = con.prepareStatement(
//                        "SELECT count(1) FROM `exception` WHERE id =?");
//                ps.setLong(1, hash);
//                return ps;
//            }
//        }, RowMapping.BOOLEAN_ROW_MAPPING);
//    }
//
//
//    public void setExceptionFilter(StrFilter exceptionFilter) {
//        this.exceptionFilter = exceptionFilter;
//    }
//
//    public void setExceptNameDAO(KVStore<String> exceptNameDAO) {
//        this.exceptNameDAO = exceptNameDAO;
//    }
//
//    public void setExceptMsgDAO(KVStore<String> exceptMsgDAO) {
//        this.exceptMsgDAO = exceptMsgDAO;
//    }
//
//    public void setStackTraceDAO(StackTraceDAO stackTraceDAO) {
//        this.stackTraceDAO = stackTraceDAO;
//    }
//
//}
