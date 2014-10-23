package com.caibowen.prma.logger.dao.impl;

import com.caibowen.prma.jdbc.JdbcAux;
import com.caibowen.prma.jdbc.mapper.RowMapping;
import com.caibowen.prma.jdbc.stmt.UpdateStatementCreator;
import com.caibowen.prma.logger.EventPO;
import com.caibowen.prma.logger.dao.EventDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author BowenCai
 * @since 22-10-2014.
 */
public class EventDAOImpl extends JdbcAux implements EventDAO {


    public static final String[] AUTO_GEN_ID = {"id"};

    @Override
    public long insert(final EventPO po) {

        return insert(new UpdateStatementCreator() {
            @Override
            public PreparedStatement createUpdate(Connection con, String[] cols) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                "INSERT INTO `event`(" +
                "`time_created`, `level`, `logger_id`, `thread_id`, `caller_sk_id`" +
               ", `flag`, `message`)VALUES(?,?,?,?,?,?,?)");
                ps.setLong(1, po.timeCreated);
                ps.setByte(2, po.level);
                ps.setInt(3, po.loggerId);
                ps.setInt(4, po.threadId);
                ps.setInt(5, po.callerSkId);
                ps.setByte(6, po.flag);
                ps.setString(7, po.message);
                return ps;
            }

            @Override
            public PreparedStatement createStatement(Connection con) throws SQLException {
                throw new UnsupportedOperationException();
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
            event id        -> insert no key back


    if exception
            except_name  -> id
            except_msg   -> id
            stack_trace  -> id
                               -> except id
            loop get all       -> id array

                event except

     */

}
