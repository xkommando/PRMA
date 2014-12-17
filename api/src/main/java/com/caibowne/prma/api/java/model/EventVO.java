package com.caibowne.prma.api.java.model;

import com.caibowen.prma.api.LogLevel;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author BowenCai
 * @since 17/12/2014.
 */
public class EventVO implements Serializable {

    private long id;
    private long timeCreated;
    private LogLevel level;
    private String loggerName;
    private String threadName;
    private StackTraceElement callerStackTrace;
    private long flag;
    private String message;

    // nullable:

    private Long reserved;
    private Map<String, String> properties;
    private List<ExceptionVO> exceptions;
    private Set<String> tags;


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

    public long getFlag() {
        return flag;
    }

    public void setFlag(long flag) {
        this.flag = flag;
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

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public List<ExceptionVO> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<ExceptionVO> exceptions) {
        this.exceptions = exceptions;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
}
