package com.caibowen.prma.api.java.model;

import com.caibowen.prma.api.model.EventVO$;
import scala.Enumeration;

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
    // non-null
    private Enumeration.Value level;
    // non-null
    private String loggerName;
    // non-null
    private String threadName;
    // non-null
    private StackTraceElement callerStackTrace;
    private long flag;
    // non-null
    private String message;

    // nullable:
    private Long reserved;
    // nullable
    private Map<String, String> properties;
    // nullable
    private List<ExceptionVO> exceptions;
    // nullable
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


    public Enumeration.Value getLevel() {
        return level;
    }

    public void setLevel(Enumeration.Value level) {
        this.level = level;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventVO)) return false;

        EventVO eventVO = (EventVO) o;

        if (flag != eventVO.flag) return false;
        if (id != eventVO.id) return false;
        if (timeCreated != eventVO.timeCreated) return false;
        if (!callerStackTrace.equals(eventVO.callerStackTrace)) return false;
        if (exceptions != null ? !exceptions.equals(eventVO.exceptions) : eventVO.exceptions != null) return false;
        if (!level.equals(eventVO.level)) return false;
        if (!loggerName.equals(eventVO.loggerName)) return false;
        if (!message.equals(eventVO.message)) return false;
        if (properties != null ? !properties.equals(eventVO.properties) : eventVO.properties != null) return false;
        if (reserved != null ? !reserved.equals(eventVO.reserved) : eventVO.reserved != null) return false;
        if (tags != null ? !tags.equals(eventVO.tags) : eventVO.tags != null) return false;
        if (!threadName.equals(eventVO.threadName)) return false;

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
        result = 31 * result + (int) (flag ^ (flag >>> 32));
        result = 31 * result + message.hashCode();
        result = 31 * result + (reserved != null ? reserved.hashCode() : 0);
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        result = 31 * result + (exceptions != null ? exceptions.hashCode() : 0);
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "com.caibowen.prma.api.java.model.EventVO{" +
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
                ", tags=" + tags +
                '}';
    }

    public static long buildFlag(Map prop, Set<String> tags , List<ExceptionVO> exceptions) {
        int sz1 = exceptions != null ? exceptions.size() : 0;
        short sz11 = tags != null? (short)tags.size() : 0;
        short sz12 = prop != null ? (short)prop.size() : 0;
        int sz2 = add(sz11, sz12);
        return add(sz1, sz2);
    }

    public static int add(short a, short b) {
        return (int)a << 16 | (int)b & 0xFFFF;
    }
    public static long add(int a, int b) {
        return ((long)a << 32) | ((long)b & 0xFFFFFFFFL);
    }

    public static boolean hasProperties(long flag) {
        return com.caibowen.prma.api.model.EventVO.hasProperties(flag);
    }
    public static boolean hasExceptions(long flag) {
        return com.caibowen.prma.api.model.EventVO.hasExceptions(flag);
    }
    public static boolean hasTags(long flag) {
        return com.caibowen.prma.api.model.EventVO.hasTags(flag);
    }
}
