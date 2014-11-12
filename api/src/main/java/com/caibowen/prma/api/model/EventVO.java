package com.caibowen.prma.api.model;

import com.caibowen.prma.api.LogLevel;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author BowenCai
 * @since 26-10-2014.
 */
public class EventVO implements Serializable {

    private static final long serialVersionUID = -8179577194579626226L;

    public long id;
    public long timeCreated;
    public LogLevel level;
    public String loggerName;
    public String threadName;

    @Nullable public StackTraceElement callerStackTrace;

    public long flag;

    public String message;

    public Long reserved;

    @Nullable
    public Map<String, Object> properties;

    @Nullable
    public List<ExceptionVO> exceptions;

    @Nullable
    public Set<String> markers;

    public EventVO() {}

    public EventVO(long timeCreated, LogLevel level, long flg, String loggerName, String threadName, StackTraceElement callerStackTrace, String message, Long reserved, Map<String, Object> properties, List<ExceptionVO> exceptions, Set<String> markers) {
        this.timeCreated = timeCreated;
        this.level = level;
        this.flag = flg;
        this.loggerName = loggerName;
        this.threadName = threadName;
        this.callerStackTrace = callerStackTrace;
        this.message = message;
        this.reserved = reserved;
        this.properties = properties;
        this.exceptions = exceptions;
        this.markers = markers;
    }

    public EventVO(long id, long timeCreated, LogLevel level, long flg, String loggerName, String threadName, StackTraceElement callerStackTrace, String message, Long reserved, Map<String, Object> properties, List<ExceptionVO> exceptions, Set<String> markers) {
        this.id = id;
        this.timeCreated = timeCreated;
        this.level = level;
        this.flag = flg;
        this.loggerName = loggerName;
        this.threadName = threadName;
        this.callerStackTrace = callerStackTrace;
        this.message = message;
        this.reserved = reserved;
        this.properties = properties;
        this.exceptions = exceptions;
        this.markers = markers;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public LogLevel getLevel() {
        return level;
    }

    public void setLevel(LogLevel level) {
        this.level = level;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public StackTraceElement getCallerStackTrace() {
        return callerStackTrace;
    }

    public void setCallerStackTrace(StackTraceElement callerStackTrace) {
        this.callerStackTrace = callerStackTrace;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getReserved() {
        return reserved;
    }

    public void setReserved(Long reserved) {
        this.reserved = reserved;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public List<ExceptionVO> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<ExceptionVO> exceptions) {
        this.exceptions = exceptions;
    }

    @Nullable
    public Set<String> getMarkers() {
        return markers;
    }

    public void setMarkers(@Nullable Set<String> markers) {
        this.markers = markers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventVO)) return false;

        EventVO eventVO = (EventVO) o;

        if (id != eventVO.id) return false;
        if (timeCreated != eventVO.timeCreated) return false;
        if (level != eventVO.level) return false;
        if (flag != eventVO.flag) return false;
        if (!threadName.equals(eventVO.threadName)) return false;
        if (!loggerName.equals(eventVO.loggerName)) return false;
        if (!message.equals(eventVO.message)) return false;

        if (reserved != null ? !reserved.equals(eventVO.reserved) : eventVO.reserved != null) return false;
        if (!callerStackTrace.equals(eventVO.callerStackTrace)) return false;
        if (exceptions != null ? !exceptions.equals(eventVO.exceptions) : eventVO.exceptions != null) return false;
        if (markers != null ? !markers.equals(eventVO.markers) : eventVO.markers != null) return false;
        if (properties != null ? !properties.equals(eventVO.properties) : eventVO.properties != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (timeCreated ^ (timeCreated >>> 32));
        result = 31 * result + level.hashCode();
        result = 31 * result + loggerName.hashCode();
        result = 31 * result + threadName.hashCode();
        result = 31 * result + (callerStackTrace != null ? callerStackTrace.hashCode() : 0);
        result = 31 * result + (int) (flag ^ (flag >>> 32));
        result = 31 * result + message.hashCode();
        result = 31 * result + (reserved != null ? reserved.hashCode() : 0);
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        result = 31 * result + (exceptions != null ? exceptions.hashCode() : 0);
        result = 31 * result + (markers != null ? markers.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "com.caibowen.prma.api.model.EventVO{" +
                "id=" + id +
                ", timeCreated=" + timeCreated +
                ", level=" + level +
                ", loggerName='" + loggerName + '\'' +
                ", threadName='" + threadName + '\'' +
                ", callerStackTrace=" + callerStackTrace +
                ", flag=" + flag +
                ", message='" + message + '\'' +
                ", reserved=" + reserved +
                ", properties=" + properties +
                ", exceptions=" + exceptions +
                ", markers=" + markers +
                '}';
    }
}
