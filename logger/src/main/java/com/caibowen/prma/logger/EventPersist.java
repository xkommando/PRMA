package com.caibowen.prma.logger;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import com.caibowen.prma.logger.dao.EventDAO;
import com.caibowen.prma.logger.dao.Int4DAO;
import com.caibowen.prma.logger.dao.PropertyDAO;
import com.caibowen.prma.logger.dao.StackTraceDAO;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author BowenCai
 * @since 23-10-2014.
 */
public class EventPersist {

    public static final StackTraceElement NA_ST = new  StackTraceElement("?", "?", "?", -1);

    Int4DAO<String> threadDAO;
    Int4DAO<String> loggerDAO;

    StackTraceDAO stackTraceDAO;
    PropertyDAO propertyDAO;

    EventDAO eventDAO;

    void xxxxxxxx(ILoggingEvent event) {


        EventPO po = getPO(event);
        final long evId = eventDAO.insert(po);


        Map<String, String> prop = getProp(event);
        if (prop != null)
            if (! propertyDAO.insert(evId, prop))
                ; // error

        IThrowableProxy tpox = event.getThrowableProxy();
        if (tpox != null) {

        }


    }


    EventPO getPO(ILoggingEvent event) {

        EventPO po = new EventPO();
        po.timeCreated = System.currentTimeMillis();

        StackTraceElement[] _callerSTs = event.getCallerData();
        StackTraceElement callerST;
        if (_callerSTs != null && _callerSTs.length > 0)
            callerST = _callerSTs[0];
        else
            callerST = NA_ST;

        final int _callerID = callerST.hashCode();
        if (! stackTraceDAO.putIfAbsent(_callerID, callerST))
            ; // -- ERROR
        po.callerSkId = _callerID;

        String _ln = event.getLoggerName();
        final int _loggerID = _ln.hashCode();
        persist(loggerDAO, _loggerID, _ln);
        po.loggerId = _loggerID;

        String _tn = event.getThreadName();
        final int _threadID = _tn.hashCode();
        persist(threadDAO, _threadID, _tn);
        po.threadId = _threadID;

        po.flag = getFlag(event);
        po.level = (byte)(event.getLevel().levelInt / Level.TRACE_INT);
        po.message = event.getFormattedMessage();

        return po;
    }

    @Nullable
    Map<String, String>
    getProp(ILoggingEvent event) {

        Map<String, String> _mdc = event.getMDCPropertyMap();
        int _mdcSz = _mdc.size();
        Map<String, String> _ctx = event.getLoggerContextVO()
                .getPropertyMap();
        int _ctxSz = _ctx.size();

        if (_mdcSz > 0 &&  _ctxSz > 0) {
            int sz = _mdcSz + _ctxSz;
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


    private static <V> void
    persist(Int4DAO<V> dao, int hash, V obj) {
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
