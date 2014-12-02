package com.caibowen.prma.store.rdb;

import com.caibowen.gplume.jdbc.JdbcSupport;
import com.caibowen.gplume.jdbc.transaction.Transaction;
import com.caibowen.gplume.jdbc.transaction.TransactionCallback;
import com.caibowen.prma.api.model.EventVO;
import com.caibowen.prma.api.model.ExceptionVO;
import com.caibowen.prma.core.StrLoader;
import com.caibowen.prma.store.EventPersist;
import com.caibowen.prma.store.rdb.dao.*;
import scala.Option;
import scala.collection.JavaConversions;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.List;

/**
 * main class for LogEvent storage
 * @author BowenCai
 * @since 23-10-2014.
 */
public class EventPersistImpl extends JdbcSupport implements EventPersist{

    @Inject Int4DAO<String> threadDAO;

    @Inject Int4DAO<String> loggerDAO;

    @Inject StackTraceDAO stackTraceDAO;

    @Inject PropertyDAO propertyDAO;

    @Inject EventDAO eventDAO;

    @Inject ExceptionDAO exceptionDAO;

    @Inject MarkerDAO markerDAO;

    @Inject final StrLoader sqls;

    public EventPersistImpl(StrLoader loader) {
        this.sqls = loader;
    }

//    final RowMapping<EventVO> VO_MAPPING = new RowMapping<EventVO>() {
//        @Override
//        public EventVO extract(@Nonnull ResultSet rs) throws SQLException {
//
//            long id = rs.getLong(1);
//            long timeCreated = rs.getLong(2);
//            int idx = (int) rs.getByte(3);
//            Enumeration.Value level = LogLevel.from(idx);
//            long flag = rs.getLong(4);
//            String message = rs.getString(5);
//            Object _r = rs.getObject(6);
//            Long reserved = _r == null ? -1L : (Long) _r;
//            // cached
//            String loggerName = loggerDAO.get(rs.getInt(7));
//            String threadName = threadDAO.get(rs.getInt(8));
//            StackTraceElement callerStackTrace = stackTraceDAO.get(rs.getInt(9));
//            scala.collection.immutable.List<ExceptionVO> exps = null;
//            scala.collection.mutable.Set<String> mks = null;
//            scala.collection.mutable.Map props = null;
//            if (EventVO.hasException(flag))
//                exps = JavaConversions.iterableAsScalaIterable(exceptionDAO.getByEvent(id));
//            if (EventVO.hasMarkers(flag))
//                mks = JavaConversions.asScalaSet(markerDAO.getByEvent(id));
//            if (EventVO.hasProperty(flag))
//                props = JavaConversions.mapAsScalaMap(propertyDAO.getByEvent(id));
//
//            return new EventVO(id, timeCreated, level, loggerName, threadName, callerStackTrace,
//                    flag, message, reserved,
//                    props, null, mks);
//        }
//    };
//
//    @Nullable
//    @Override
//    public EventVO get(final long eventId) {
//        final EventVO vo = queryForObject(new StatementCreator() {
//            @Nonnull
//            @Override
//            public PreparedStatement createStatement(@Nonnull Connection con) throws SQLException {
//                PreparedStatement ps = con.prepareStatement(
//                        sqls.get("EventPersist.get")
//                );
//                ps.setLong(1, eventId);
//                return ps;
//            }
//        }, VO_MAPPING);
//        if (vo == null)
//            return null;
//        return vo;
//    }
//
//    @Override
//    public List<EventVO> getWithException(final long minTime, final int limit) {
//        List<EventVO> vos = queryForList(new StatementCreator() {
//            @Nonnull
//            @Override
//            public PreparedStatement createStatement(@Nonnull Connection con) throws SQLException {
//                PreparedStatement ps = con.prepareStatement(
//                        sqls.get("EventPersist.getWithException")
//                );
//                ps.setLong(1, minTime);
//                ps.setInt(2, limit);
//                return ps;
//            }
//        }, VO_MAPPING);
//        if (vos.isEmpty())
//            return vos;
//
//        return vos;
//    }

    public long persist(final EventVO event) {

        return execute(new TransactionCallback<Long>() {
            @Override
            public Long withTransaction(@Nonnull Transaction tnx) throws Exception {
                EventDO po = getDO(event);
                final long evId = eventDAO.insert(po);
                Option<scala.collection.immutable.Map<String, Object>> op = event.getProperties();
                if (op.isDefined()) {
                    Map prop = JavaConversions.mapAsJavaMap(op.get());
                    if (!propertyDAO.insertIfAbsent(evId, prop))
                        throw new RuntimeException("cannot insert properties[" + prop + "]"); // error
                }
                Option<scala.collection.immutable.List<ExceptionVO>> op2 = event.getExceptions();
                if (op2.isDefined()) {
                    scala.collection.immutable.List<ExceptionVO> tpox = op2.get();
                    exceptionDAO.insertIfAbsent(evId, tpox);
                }
                Option<scala.collection.immutable.Set<String>> op3 = event.getMarkers();
                if (op3.isDefined()) {
                    Set<String> mks = JavaConversions.setAsJavaSet(op3.get());
                    markerDAO.insertIfAbsent(evId, mks);
                }
                return evId;
            }
        });
    }

    @Override
    public void batchPersist(final List<EventVO> ls) {
        execute(new TransactionCallback<Object>() {
            @Override
            public Object withTransaction(@Nonnull Transaction transaction) throws Exception {

                ArrayList<EventDO> dols = new ArrayList<EventDO>(ls.size());
                for (EventVO _e : ls)
                    dols.add(getDO(_e));

                final java.util.List<Long> ids = eventDAO.batchInsert(dols);

                for (int i = 0; i < ids.size(); i++) {
                    EventVO event = ls.get(i);
                    long evId = ids.get(i);

                    Option<scala.collection.immutable.Map<String, Object>> op = event.getProperties();
                    if (op.isDefined()) {
                        Map prop = JavaConversions.mapAsJavaMap(op.get());
                        if (!propertyDAO.insertIfAbsent(evId, prop))
                            throw new RuntimeException("cannot insert properties[" + prop + "]"); // error
                    }
                    Option<scala.collection.immutable.List<ExceptionVO>> op2 = event.getExceptions();
                    if (op2.isDefined()) {
                        scala.collection.immutable.List<ExceptionVO> tpox = op2.get();
                        exceptionDAO.insertIfAbsent(evId, tpox);
                    }
                    Option<scala.collection.immutable.Set<String>> op3 = event.getMarkers();
                    if (op3.isDefined()) {
                        Set<String> mks = JavaConversions.setAsJavaSet(op3.get());
                        markerDAO.insertIfAbsent(evId, mks);
                    }
                }

                return null;
            }
        });
    }

    protected EventDO getDO(EventVO event) {

        EventDO vo = new EventDO();
        vo.timeCreated = event.getTimeCreated();

        StackTraceElement callerST = event.getCallerStackTrace();
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

        vo.flag = event.getFlag();
        vo.level = (byte)event.getLevel().id();
        vo.message = event.getMessage();
        return vo;
    }


    protected static <V> void
    persist(Int4DAO<V> dao, int hash, V obj) {
        if ( !dao.putIfAbsent(hash, obj))
            throw new RuntimeException(dao.toString()
                    + "  could not save value["
                    + (null == obj ? "null" : obj.toString()) + "] with id " + hash);
    }


    public Int4DAO<String> getThreadDAO() {
        return threadDAO;
    }

    public void setThreadDAO(Int4DAO<String> threadDAO) {
        this.threadDAO = threadDAO;
    }

    public Int4DAO<String> getLoggerDAO() {
        return loggerDAO;
    }

    public void setLoggerDAO(Int4DAO<String> loggerDAO) {
        this.loggerDAO = loggerDAO;
    }

    public StackTraceDAO getStackTraceDAO() {
        return stackTraceDAO;
    }

    public void setStackTraceDAO(StackTraceDAO stackTraceDAO) {
        this.stackTraceDAO = stackTraceDAO;
    }

    public PropertyDAO getPropertyDAO() {
        return propertyDAO;
    }

    public void setPropertyDAO(PropertyDAO propertyDAO) {
        this.propertyDAO = propertyDAO;
    }

    public EventDAO getEventDAO() {
        return eventDAO;
    }

    public void setEventDAO(EventDAO eventDAO) {
        this.eventDAO = eventDAO;
    }

    public ExceptionDAO getExceptionDAO() {
        return exceptionDAO;
    }

    public void setExceptionDAO(ExceptionDAO exceptionDAO) {
        this.exceptionDAO = exceptionDAO;
    }

    public MarkerDAO getMarkerDAO() {
        return markerDAO;
    }

    public void setMarkerDAO(MarkerDAO markerDAO) {
        this.markerDAO = markerDAO;
    }

}
