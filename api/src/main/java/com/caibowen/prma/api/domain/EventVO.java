package com.caibowen.prma.api.domain;

import com.caibowen.prma.api.LogLevel;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Map;

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

    public StackTraceElement callerStackTrace;

    public String message;

    @Nullable Map<String, Object> properties;
    @Nullable Map<String, Throwable> exceptions;

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

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public Map<String, Throwable> getExceptions() {
        return exceptions;
    }

    public void setExceptions(Map<String, Throwable> exceptions) {
        this.exceptions = exceptions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventVO)) return false;

        EventVO event = (EventVO) o;

        if (id != event.id) return false;
        if (timeCreated != event.timeCreated) return false;
        if (level != event.level) return false;
        if (!loggerName.equals(event.loggerName)) return false;
        if (!message.equals(event.message)) return false;
        if (!threadName.equals(event.threadName)) return false;
        if (!callerStackTrace.equals(event.callerStackTrace)) return false;
        if (exceptions != null ? !exceptions.equals(event.exceptions) : event.exceptions != null) return false;
        if (properties != null ? !properties.equals(event.properties) : event.properties != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (timeCreated ^ (timeCreated >>> 32));
        result = 31 * result + level.hashCode();
        result = 31 * result + loggerName.hashCode();
        result = 31 * result + threadName.hashCode();
        result = 31 * result + callerStackTrace.hashCode();
        result = 31 * result + message.hashCode();
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        result = 31 * result + (exceptions != null ? exceptions.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", timeCreated=" + timeCreated +
                ", level=" + level +
                ", loggerName='" + loggerName + '\'' +
                ", threadName='" + threadName + '\'' +
                ", callerStackTrace=" + callerStackTrace +
                ", message='" + message + '\'' +
                ", properties=" + properties +
                ", exceptions=" + exceptions +
                '}';
    }
}
