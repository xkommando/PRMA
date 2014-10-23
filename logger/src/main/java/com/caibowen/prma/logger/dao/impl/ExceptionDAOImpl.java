package com.caibowen.prma.logger.dao.impl;

import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import com.caibowen.prma.common.Hashing;
import com.caibowen.prma.logger.dao.ExceptionDAO;
import com.caibowen.prma.logger.dao.Int4DAO;
import com.caibowen.prma.logger.dao.StackTraceDAO;

/**
 * @author BowenCai
 * @since 23-10-2014.
 */
public class ExceptionDAOImpl implements ExceptionDAO {

    @Override
    public boolean insert(long eventId, IThrowableProxy prop) {
        return false;
    }


    Int4DAO<String> thwNameDAO;
    Int4DAO<String> exceptMsgDAO;

    StackTraceDAO stackTraceDAO;

    private static <V> void
    persist(Int4DAO<V> dao, int hash, V obj) {
        if ( !dao.putIfAbsent(hash, obj))
            ; // report ERROR!!!
    }


    //    if (event.getThrowableProxy() != null)
    void throwables(int eventID, IThrowableProxy tpox, int seq) {

        String _thwName = tpox.getClassName();
        final int thwID = _thwName.hashCode();
        persist(thwNameDAO, thwID, _thwName);

        String _msg = tpox.getMessage();
        final int msgID = _msg.hashCode();
        long expID =  (long)thwID << 32 | msgID & 0xFFFFFFFFL;
        persist(exceptMsgDAO, msgID, _msg);

        int[] ids;
        StackTraceElementProxy[] stps = tpox.getStackTraceElementProxyArray();
        if (stps != null && stps.length > 0) {
            ids = new int[stps.length];
            for (int i = 0; i < stps.length; i++) {
                StackTraceElement st = stps[i].getStackTraceElement();
                int id = st.hashCode();
                if (!stackTraceDAO.putIfAbsent(id, st))
                    ; // error !

                ids[i] = id;
                expID = Hashing.hash128To64(expID, Hashing.twang_mix64((long) id));
            }
        }
//        PreparedStatement p;
//        p.setNull(0, Types.BINARY);

        IThrowableProxy next = tpox.getCause();
        if (next != null)
            throwables(eventID, next, ++seq);

    }

}
