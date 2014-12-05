//package com.caibowen.prma.store.rdb.dao.impl;
//
//import com.caibowen.gplume.jdbc.JdbcSupport;
//import com.caibowen.gplume.jdbc.StatementCreator;
//import com.caibowen.gplume.jdbc.mapper.RowMapping;
//import com.caibowen.prma.core.StrLoader;
//import com.caibowen.prma.store.rdb.dao.EventDAO;
//import com.caibowen.prma.store.rdb.EventDO;
//
//import javax.annotation.Nonnull;
//import javax.inject.Inject;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//import java.sql.Types;
//import java.util.List;
//
///**
//* @author BowenCai
//* @since 22-10-2014.
//*/
//public class EventDAOImpl extends JdbcSupport implements EventDAO {
//
//    public static final String[] AUTO_GEN_ID = {"id"};
//
//    @Inject
//    final StrLoader sqls;
//
//    public EventDAOImpl(StrLoader loader) {
//        this.sqls = loader;
//    }
//
//    @Override
//    public long insert(final EventDO po) {
//
//        return insert(new StatementCreator() {
//            @Override
//            public PreparedStatement createStatement(Connection con) throws SQLException {
//                PreparedStatement ps = con.prepareStatement(
//                sqls.get("EventDAO.insert"), AUTO_GEN_ID);
//                ps.setLong(1, po.timeCreated);
//                ps.setByte(2, po.level);
//                ps.setInt(3, po.loggerId);
//                ps.setInt(4, po.threadId);
//                if (po.callerSkId != null)
//                    ps.setInt(5, po.callerSkId);
//                else
//                    ps.setNull(5, Types.INTEGER);
//
//                ps.setLong(6, po.flag);
//                ps.setString(7, po.message);
//
//                if (po.reserved != null)
//                    ps.setLong(8, po.reserved);
//                else
//                    ps.setNull(8, Types.BIGINT);
//
//                return ps;
//            }
//        }, AUTO_GEN_ID, RowMapping.LONG_ROW_MAPPING);
//    }
//
//    @Override
//    public List<Long> batchInsert(final List<EventDO> ls) {
//        return batchInsert(new StatementCreator() {
//            @Nonnull
//            @Override
//            public PreparedStatement createStatement(@Nonnull Connection con) throws SQLException {
//                PreparedStatement ps = con.prepareStatement(
//                        sqls.get("EventDAO.insert"), AUTO_GEN_ID);
//
//                for (EventDO po : ls) {
//                    ps.setLong(1, po.timeCreated);
//                    ps.setByte(2, po.level);
//                    ps.setInt(3, po.loggerId);
//                    ps.setInt(4, po.threadId);
//                    if (po.callerSkId != null)
//                        ps.setInt(5, po.callerSkId);
//                    else
//                        ps.setNull(5, Types.INTEGER);
//
//                    ps.setLong(6, po.flag);
//                    ps.setString(7, po.message);
//
//                    if (po.reserved != null)
//                        ps.setLong(8, po.reserved);
//                    else
//                        ps.setNull(8, Types.BIGINT);
//
//                    ps.addBatch();
//                }
//                return ps;
//            }
//        }, AUTO_GEN_ID, RowMapping.LONG_ROW_MAPPING);
//    }
//
//    /*
//
//    determine flag
//
//    caller stack_trace -> id
//    logger              -> id
//    thread              -> id
//
//
//    flag
//    msg
//    level
//    time created
//
//                                -> event id: uncompressed -> 30B + msg
//
//    if property
//            property id
//            event id        -> insertIfAbsent no key back
//
//
//    if exception
//            except_name  -> id
//            except_msg   -> id
//            stack_trace  -> ids
//                               -> except id
//            loop at all       -> id array
//
//                event except
//
//     */
//
//}
