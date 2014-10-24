package com.caibowen.prma.store;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import com.caibowen.prma.store.dao.*;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

/**
 * @author BowenCai
 * @since 23-10-2014.
 */
public class EventPersist {

    @Inject Int4DAO<String> threadDAO;

    @Inject Int4DAO<String> loggerDAO;

    @Inject StackTraceDAO stackTraceDAO;

    @Inject PropertyDAO propertyDAO;

    @Inject EventDAO eventDAO;

    @Inject ExceptionDAO exceptionDAO;


    public void xxxxxxxx(ILoggingEvent event) {

        EventDO po = getVO(event);
        final long evId = eventDAO.insert(po);


        Map<String, String> prop = LogEventAux.extractProperties(event);
        if (prop != null)
            if (! propertyDAO.insert(evId, prop))
                ; // error

        IThrowableProxy tpox = event.getThrowableProxy();
        if (tpox != null) {
            exceptionDAO.insert(evId, tpox);
        }

    }


    private EventDO getVO(ILoggingEvent event) {

        EventDO vo = new EventDO();
        vo.timeCreated = System.currentTimeMillis();

        StackTraceElement callerST = LogEventAux.callerST(event);
        final int _callerID = callerST.hashCode();
        if (! stackTraceDAO.putIfAbsent(_callerID, callerST))
            ; // -- ERROR
        vo.callerSkId = _callerID;

        String _ln = event.getLoggerName();
        final int _loggerID = _ln.hashCode();
        persist(loggerDAO, _loggerID, _ln);
        vo.loggerId = _loggerID;

        String _tn = event.getThreadName();
        final int _threadID = _tn.hashCode();
        persist(threadDAO, _threadID, _tn);
        vo.threadId = _threadID;

        vo.flag = LogEventAux.flag(event);
        vo.level = LogEventAux.level(event);
        vo.message = event.getFormattedMessage();

        return vo;
    }



    private static <V> void
    persist(Int4DAO<V> dao, int hash, V obj) {
        if ( !dao.putIfAbsent(hash, obj))
            ; // report ERROR!!!
    }



}
