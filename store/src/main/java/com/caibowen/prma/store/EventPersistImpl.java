package com.caibowen.prma.store;

import com.caibowen.gplume.jdbc.transaction.Transaction;
import com.caibowen.gplume.jdbc.transaction.TransactionCallback;
import com.caibowen.prma.api.model.EventVO;
import com.caibowen.prma.api.model.ExceptionVO;
import com.caibowen.prma.store.dao.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    @Inject MarkerDAO markerDAO;

    public void persist(final EventVO event) {

        eventDAO.execute(new TransactionCallback<Object>() {
            @Override
            public Object withTransaction(@Nonnull Transaction tnx) throws Exception {
                EventDO po = getDO(event);
                final long evId = eventDAO.insert(po);

                Map prop = event.properties;
                if (prop != null)
                    if (! propertyDAO.insertIfAbsent(evId, prop))
                        throw new RuntimeException("cannot insert properties[" + prop + "]"); // error

                List<ExceptionVO> tpox = event.exceptions;
                if (tpox != null && tpox.size() > 0)
                    exceptionDAO.insertIfAbsent(evId, tpox);

                Set<String> mks = event.markers;
                if (mks != null && mks.size() > 0)
                    markerDAO.insertIfAbsent(evId, mks);

                return null;
            }
        });
    }

    @Override
    public void batchPersist(final List<EventVO> ls) {
        eventDAO.execute(new TransactionCallback<Object>() {
            @Override
            public Object withTransaction(@Nonnull Transaction transaction) throws Exception {

                List<EventDO> dols = new ArrayList<EventDO>(ls.size());
                for (EventVO _e : ls)
                    dols.add(getDO(_e));

                final List<Long> ids = eventDAO.batchInsert(dols);

                for (int i = 0; i < ids.size(); i++) {
                    EventVO event = ls.get(i);
                    long evId = ids.get(i);
                    Map prop = event.properties;
                    if (prop != null)
                        if (! propertyDAO.insertIfAbsent(evId, prop))
                            throw new RuntimeException("cannot insert properties[" + prop + "]"); // error

                    List<ExceptionVO> tpox = event.exceptions;
                    if (tpox != null)
                        exceptionDAO.insertIfAbsent(evId, tpox);


                    Set<String> mks = event.markers;
                    if (mks != null && mks.size() > 0)
                        markerDAO.insertIfAbsent(evId, mks);
                }

                return null;
            }
        });
    }

    private EventDO getDO(EventVO event) {

        EventDO vo = new EventDO();
        vo.timeCreated = event.timeCreated;

        StackTraceElement callerST = event.callerStackTrace;
        if (callerST != null) {
            final int _callerID = callerST.hashCode();
            if (!stackTraceDAO.putIfAbsent(_callerID, callerST))
                throw new RuntimeException("could not save stack trace "
                        + callerST.toString());
            vo.callerSkId = _callerID;
        }

        String _ln = event.getLoggerName();
        final int _loggerID = _ln.hashCode();
        persist(loggerDAO, _loggerID, _ln);
        vo.loggerId = _loggerID;

        String _tn = event.getThreadName();
        final int _threadID = _tn.hashCode();
        persist(threadDAO, _threadID, _tn);
        vo.threadId = _threadID;

        vo.flag = event.flag;
        vo.level = (byte)event.level.levelInt;
        vo.message = event.message;

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
