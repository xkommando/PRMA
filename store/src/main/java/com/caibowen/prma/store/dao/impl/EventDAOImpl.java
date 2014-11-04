package com.caibowen.prma.store.dao.impl;

import com.caibowen.gplume.jdbc.JdbcSupport;
import com.caibowen.gplume.jdbc.StatementCreator;
import com.caibowen.gplume.jdbc.mapper.RowMapping;
import com.caibowen.prma.store.EventDO;
import com.caibowen.prma.store.dao.EventDAO;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * @author BowenCai
 * @since 22-10-2014.
 */
public class EventDAOImpl extends JdbcSupport implements EventDAO {

    public static final String[] AUTO_GEN_ID = {"id"};

    @Override
    public long insert(final EventDO po) {

        return insert(new StatementCreator() {
            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                "INSERT INTO `event`(" +
                "`time_created`, `level`, `logger_id`, `thread_id`, `caller_sk_id`" +
               ", `flag`, `message`)VALUES(?,?,?,?,?,?,?)", AUTO_GEN_ID);
                ps.setLong(1, po.timeCreated);
                ps.setByte(2, po.level);
                ps.setInt(3, po.loggerId);
                ps.setInt(4, po.threadId);
                ps.setInt(5, po.callerSkId);
                ps.setByte(6, po.flag);
                ps.setString(7, po.message);
                return ps;
            }
        }, AUTO_GEN_ID, RowMapping.LONG_ROW_MAPPING);
    }

    @Override
    public List<Long> batchInsert(final List<EventDO> ls) {
        return batchInsert(new StatementCreator() {
            @Nonnull
            @Override
            public PreparedStatement createStatement(@Nonnull Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO `event`(" +
                                "`time_created`, `level`, `logger_id`, `thread_id`, `caller_sk_id`" +
                                ", `flag`, `message`)VALUES(?,?,?,?,?,?,?)", AUTO_GEN_ID);

                for (EventDO po : ls) {
                    ps.setLong(1, po.timeCreated);
                    ps.setByte(2, po.level);
                    ps.setInt(3, po.loggerId);
                    ps.setInt(4, po.threadId);
                    ps.setInt(5, po.callerSkId);
                    ps.setByte(6, po.flag);
                    ps.setString(7, po.message);
                    ps.addBatch();
                }
                return ps;
            }
        }, AUTO_GEN_ID, RowMapping.LONG_ROW_MAPPING);
    }

    /*

    determine flag

    caller stack_trace -> id
    logger              -> id
    thread              -> id


    flag
    msg
    level
    time created

                                -> event id: uncompressed -> 30B + msg

    if property
            property id
            event id        -> insertIfAbsent no key back


    if exception
            except_name  -> id
            except_msg   -> id
            stack_trace  -> ids
                               -> except id
            loop get all       -> id array

                event except

     */

}
