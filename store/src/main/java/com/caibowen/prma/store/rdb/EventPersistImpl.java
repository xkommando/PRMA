package com.caibowen.prma.store.rdb;

import com.caibowen.gplume.jdbc.JdbcSupport;
import com.caibowen.gplume.jdbc.StatementCreator;
import com.caibowen.gplume.jdbc.mapper.RowMapping;
import com.caibowen.gplume.jdbc.transaction.Transaction;
import com.caibowen.gplume.jdbc.transaction.TransactionCallback;
import com.caibowen.prma.api.FlagABI;
import com.caibowen.prma.api.LogLevel;
import com.caibowen.prma.api.model.EventVO;
import com.caibowen.prma.api.model.ExceptionVO;
import com.caibowen.prma.core.StringLoader;
import com.caibowen.prma.store.EventPersist;
import com.caibowen.prma.store.rdb.dao.*;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * main class for LogEvent storage
 * @author BowenCai
 * @since 23-10-2014.
 */
public class EventPersistImpl extends JdbcSupport implements EventPersist {

    @Inject Int4DAO<String> threadDAO;

    @Inject Int4DAO<String> loggerDAO;

    @Inject StackTraceDAO stackTraceDAO;

    @Inject PropertyDAO propertyDAO;

    @Inject EventDAO eventDAO;

    @Inject ExceptionDAO exceptionDAO;

    @Inject MarkerDAO markerDAO;

    @Inject StringLoader sqls;

    final RowMapping<EventVO> VO_MAPPING = new RowMapping<EventVO>() {
        @Override
        public EventVO extract(@Nonnull ResultSet rs) throws SQLException {
            EventVO vo = new EventVO();
            vo.id = rs.getLong(1);
            vo.timeCreated = rs.getLong(2);
            int idx = (int)rs.getByte(3);
            vo.level = LogLevel.values()[4 - idx / 2];
            vo.flag = rs.getLong(4);
            vo.message = rs.getString(5);
            Object _r = rs.getObject(6);
            vo.reserved = _r == null ? null : (Long)_r;
            // cached
            vo.loggerName = loggerDAO.get(rs.getInt(7));
            vo.threadName = threadDAO.get(rs.getInt(8));
            vo.callerStackTrace = stackTraceDAO.get(rs.getInt(9));
            return vo;
        }
    };

    @Nullable
    @Override
    public EventVO get(final long eventId) {
        final EventVO vo = queryForObject(new StatementCreator() {
            @Nonnull
            @Override
            public PreparedStatement createStatement(@Nonnull Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        sqls.get("EventPersist.get")
                );
                ps.setLong(1, eventId);
                return ps;
            }
        }, VO_MAPPING);
        if (vo == null)
            return null;
        normalize(vo);
        return vo;
    }

    @Override
    public List<EventVO> getWithException(final long minTime, final int limit) {
        List<EventVO> vos = queryForList(new StatementCreator() {
            @Nonnull
            @Override
            public PreparedStatement createStatement(@Nonnull Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        sqls.get("EventPersist.getWithException")
                );
                ps.setLong(1, minTime);
                ps.setInt(2, limit);
                return ps;
            }
        }, VO_MAPPING);
        if (vos.isEmpty())
            return vos;

        for (EventVO vo : vos)
            normalize(vo);

        return vos;
    }

    // pushBack attached fields that is cached in other DAOs
    protected void normalize(EventVO vo) {
        final long eventId = vo.id;
        final long flag = vo.flag;
        if (FlagABI.exceptionCount(flag) > 0)
            vo.exceptions = exceptionDAO.getByEvent(eventId);
        if (FlagABI.markerCount(flag) > 0)
            vo.markers = markerDAO.getByEvent(eventId);
        if (FlagABI.propertyCount(flag) > 0)
            vo.properties = propertyDAO.getByEvent(eventId);
    }

    public long persist(final EventVO event) {

        return execute(new TransactionCallback<Long>() {
            @Override
            public Long withTransaction(@Nonnull Transaction tnx) throws Exception {
                EventDO po = getDO(event);
                final long evId = eventDAO.insert(po);

                Map prop = event.properties;
                if (prop != null && prop.size() > 0)
                    if (!propertyDAO.insertIfAbsent(evId, prop))
                        throw new RuntimeException("cannot insert properties[" + prop + "]"); // error

                List<ExceptionVO> tpox = event.exceptions;
                if (tpox != null && tpox.size() > 0)
                    exceptionDAO.insertIfAbsent(evId, tpox);

                Set<String> mks = event.markers;
                if (mks != null && mks.size() > 0)
                    markerDAO.insertIfAbsent(evId, mks);

                return evId;
            }
        });
    }

    @Override
    public void batchPersist(final List<EventVO> ls) {
        execute(new TransactionCallback<Object>() {
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
                        if (!propertyDAO.insertIfAbsent(evId, prop))
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

    public StringLoader getSqls() {
        return sqls;
    }

    public void setSqls(StringLoader sqls) {
        this.sqls = sqls;
    }
}
