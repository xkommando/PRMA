package com.caibowen.prma.logger.db.impl;

import ch.qos.logback.classic.Level;

import java.sql.PreparedStatement;
import java.util.HashMap;

/**
 * @author BowenCai
 * @since 22-10-2014.
 */
public class H2EventDAO {

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
    void s() throws Throwable {
    }
}
