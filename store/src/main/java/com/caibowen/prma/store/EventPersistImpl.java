package com.caibowen.prma.store;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import com.caibowen.gplume.jdbc.transaction.Transaction;
import com.caibowen.gplume.jdbc.transaction.TransactionCallback;
import com.caibowen.prma.store.dao.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Map;

/**
 * main class for LogEvent storage
 * @author BowenCai
 * @since 23-10-2014.
 */
public class EventPersistImpl implements EventPersist {

    @Inject Int4DAO<String> threadDAO;

    @Inject Int4DAO<String> loggerDAO;

    @Inject StackTraceDAO stackTraceDAO;

    @Inject PropertyDAO propertyDAO;

    @Inject EventDAO eventDAO;

    @Inject ExceptionDAO exceptionDAO;


    public void persist(final ILoggingEvent event) {

        eventDAO.execute(new TransactionCallback<Object>() {
            @Override
            public Object withTransaction(@Nonnull Transaction tnx) throws Exception {
                EventDO po = getDO(event);
                final long evId = eventDAO.insert(po);


                Map<String, String> prop = LogEventAux.extractProperties(event);
                if (prop != null)
                    if (! propertyDAO.insertAll(evId, prop))
                        ; // error

                IThrowableProxy tpox = event.getThrowableProxy();
                if (tpox != null) {
                    exceptionDAO.insert(evId, tpox);
                }
                return null;
            }
        });
    }


    private EventDO getDO(ILoggingEvent event) {

        EventDO vo = new EventDO();
        vo.timeCreated = System.currentTimeMillis();

        StackTraceElement callerST = LogEventAux.callerST(event);
        final int _callerID = callerST.hashCode();
        if (! stackTraceDAO.putIfAbsent(_callerID, callerST))
            throw new RuntimeException("could not save stack trace "
                    + callerST.toString());

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
            throw new RuntimeException(dao.toString()
                    + "  could not save value["
                    + (null == obj ? "null" : obj.toString()) + "] with id " + hash);
    }



}
