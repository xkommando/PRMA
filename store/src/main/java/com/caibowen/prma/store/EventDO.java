package com.caibowen.prma.store;

import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * data object representing event in the DB
 *
 * @author BowenCai
 * @since 23-10-2014.
 */
public class EventDO implements Serializable {

    private static final long serialVersionUID = -8809863102710979910L;

    /**
     *  auto generated
     */
    public long id;

    /**
     * ms
     */
    public long timeCreated;

    /**
     * byte level = (byte)(event.getLevel().levelInt / Level.TRACE_INT);
     */
    public byte level;

    public long flag;
    /**
     * logger name id, usually the hashCode() of the name string
     */
    public int loggerId;

    /**
     * thread name id, usually the hashCode() of the name string
     */
    public int threadId;

    /**
     * caller stacktrace id
     */
    @Nullable public Integer callerSkId;

    /**
     * indicate properties, exception
     */
    @Nullable public Long reserved;

    /**
     * formatted message. length < 2047
     */
    public String message;


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

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public long getFlag() {
        return flag;
    }

    public void setFlag(long flag) {
        this.flag = flag;
    }

    public int getLoggerId() {
        return loggerId;
    }

    public void setLoggerId(int loggerId) {
        this.loggerId = loggerId;
    }

    public int getThreadId() {
        return threadId;
    }

    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }

    @Nullable
    public Integer getCallerSkId() {
        return callerSkId;
    }

    public void setCallerSkId(@Nullable Integer callerSkId) {
        this.callerSkId = callerSkId;
    }

    @Nullable
    public Long getReserved() {
        return reserved;
    }

    public void setReserved(@Nullable Long reserved) {
        this.reserved = reserved;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventDO)) return false;

        EventDO eventDO = (EventDO) o;

        if (flag != eventDO.flag) return false;
        if (id != eventDO.id) return false;
        if (level != eventDO.level) return false;
        if (loggerId != eventDO.loggerId) return false;
        if (threadId != eventDO.threadId) return false;
        if (timeCreated != eventDO.timeCreated) return false;
        if (callerSkId != null ? !callerSkId.equals(eventDO.callerSkId) : eventDO.callerSkId != null) return false;
        if (!message.equals(eventDO.message)) return false;
        if (reserved != null ? !reserved.equals(eventDO.reserved) : eventDO.reserved != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (timeCreated ^ (timeCreated >>> 32));
        result = 31 * result + (int) level;
        result = 31 * result + (int) (flag ^ (flag >>> 32));
        result = 31 * result + loggerId;
        result = 31 * result + threadId;
        result = 31 * result + (callerSkId != null ? callerSkId.hashCode() : 0);
        result = 31 * result + (reserved != null ? reserved.hashCode() : 0);
        result = 31 * result + message.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "com.caibowen.prma.store.EventDO{" +
                "id=" + id +
                ", timeCreated=" + timeCreated +
                ", level=" + level +
                ", flag=" + flag +
                ", loggerId=" + loggerId +
                ", threadId=" + threadId +
                ", callerSkId=" + callerSkId +
                ", reserved=" + reserved +
                ", message='" + message + '\'' +
                '}';
    }
}
