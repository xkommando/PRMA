package com.caibowen.prma.logger.db;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import com.caibowen.prma.common.Hashing;

import javax.annotation.Nullable;
import java.sql.PreparedStatement;
import java.sql.SQLType;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * @author BowenCai
 * @since 22-10-2014.
 */
public class EventPersister {

    public static final String NA = "?";
    public static final StackTraceElement NA_ST = new  StackTraceElement(NA, NA, NA, -1);

    IntDAO<String> threadDAO;
    IntDAO<String> loggerDAO;
    IntDAO<String> thwNameDAO;
    IntDAO<String> exceptMsgDAO;

    IntDAO<StackTraceElement> stackTraceDAO;
    PropertyDAO propertyDAO;


    void event(ILoggingEvent event) {
        StackTraceElement[] _callerSTs = event.getCallerData();
        StackTraceElement callerST =
        if (_callerSTs != null && _callerSTs.length > 0)
            callerST = _callerSTs[0];
        else
            callerST = NA_ST;

        final int callerID = callerST.hashCode();
        persist(stackTraceDAO, callerID, callerST);

        String _ln = event.getLoggerName();
        final int loggerID = _ln.hashCode();
        persist(loggerDAO, loggerID, _ln);

        String _tn = event.getThreadName();
        final int threadID = _tn.hashCode();
        persist(threadDAO, threadID, _tn);

        final byte flag = getFlag(event);
        final byte level = (byte)(event.getLevel().levelInt / Level.TRACE_INT);

        final long timeCreated = System.currentTimeMillis();
        final String msg = event.getFormattedMessage();

        // save event;


    }


    void prop(int eventID, ILoggingEvent event) {
        Map prop = getProp(event);
        if (prop == null)
            return;

//        propertyDAO.putAllIfAbsent(prop);

    }

//    if (event.getThrowableProxy() != null)
    void throwables(int eventID, IThrowableProxy tpox, int seq) {

        String _thwName = tpox.getClassName();
        final int thwID = _thwName.hashCode();
        persist(thwNameDAO, thwID, _thwName);

        String _msg = tpox.getMessage();
        final int msgID = _msg.hashCode();
        long expID =  (long)thwID << 32 | msgID & 0xFFFFFFFFL;
        tpox.getCause()
        persist(exceptMsgDAO, msgID, _msg);
        int[] ids;
        StackTraceElementProxy[] stps = tpox.getStackTraceElementProxyArray();
        if (stps != null && stps.length > 0) {
            ids = new int[stps.length];
            for (int i = 0; i < stps.length; i++) {
                StackTraceElement st = stps[i].getStackTraceElement();
                int id = st.hashCode();
                persist(stackTraceDAO, id, st);
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

    @Nullable Map<String, String>
    getProp(ILoggingEvent event) {

        Map<String, String> _mdc = event.getMDCPropertyMap();
        int _mdcSz = _mdc.size();
        Map<String, String> _ctx = event.getLoggerContextVO()
                .getPropertyMap();
        int _ctxSz = _ctx.size();

        if (_mdcSz > 0 &&  _ctxSz > 0) {
            int sz = _mdcSz + _ctxSz
            Map<String, String> ret = new HashMap(sz * 4 / 3 + 1);
            ret.putAll(_mdc);
            ret.putAll(_ctx);
            return ret;
        } else if (_mdcSz > 0)
            return _mdc;
        else if (_ctxSz > 0)
            return _ctx;
        else
            return null;
    }

//    Map<String, String> mergedMap = mergePropertyMaps(event);
//    insertProperties(mergedMap, connection, eventId);

//    protected void insertProperties(Map<String, String> mergedMap,
//                                    Connection connection, long eventId) throws SQLException {
//        Set<String> propertiesKeys = mergedMap.keySet();
//        if (propertiesKeys.size() > 0) {
//            PreparedStatement insertPropertiesStatement = null;
//            try {
//                insertPropertiesStatement = connection
//                        .prepareStatement(insertPropertiesSQL);
//
//                for (String key : propertiesKeys) {
//                    String value = mergedMap.get(key);
//
//                    insertPropertiesStatement.setLong(1, eventId);
//                    insertPropertiesStatement.setString(2, key);
//                    insertPropertiesStatement.setString(3, value);
//
//                    if (cnxSupportsBatchUpdates) {
//                        insertPropertiesStatement.addBatch();
//                    } else {
//                        insertPropertiesStatement.execute();
//                    }
//                }
//
//                if (cnxSupportsBatchUpdates) {
//                    insertPropertiesStatement.executeBatch();
//                }
//            } finally {
//                closeStatement(insertPropertiesStatement);
//            }
//        }
//    }
    Map<String, String> mergePropertyMaps(ILoggingEvent event) {
        Map<String, String> mergedMap = new HashMap<String, String>();
        // we add the context properties first, then the event properties, since
        // we consider that event-specific properties should have priority over
        // context-wide properties.
        Map<String, String> loggerContextMap = event.getLoggerContextVO()
                .getPropertyMap();
        Map<String, String> mdcMap = event.getMDCPropertyMap();
        if (loggerContextMap != null) {
            mergedMap.putAll(loggerContextMap);
        }
        if (mdcMap != null) {
            mergedMap.putAll(mdcMap);
        }

        return mergedMap;
    }




    private static <V> void
    persist(IntDAO<V> dao, int hash, V obj) {
        if ( !dao.putIfAbsent(hash, obj))
            ; // report ERROR!!!
    }






    public static final short PROPERTIES_EXIST = 0x01;
    public static final short EXCEPTION_EXISTS = 0x02;

    public static byte getFlag(ILoggingEvent event) {
        byte mask = 0;
//
//        int mdcPropSize = 0;
//        if (event.getMDCPropertyMap() != null) {
//            mdcPropSize = event.getMDCPropertyMap().keySet().size();
//        }
//        int contextPropSize = 0;
//        if (event.getLoggerContextVO().getPropertyMap() != null) {
//            contextPropSize = event.getLoggerContextVO().getPropertyMap().size();
//        }
//
//        if (mdcPropSize > 0 || contextPropSize > 0) {
//            mask = PROPERTIES_EXIST;
//        }
//        if (event.getThrowableProxy() != null) {
//            mask |= EXCEPTION_EXISTS;
//        }
        return mask;
    }
}
