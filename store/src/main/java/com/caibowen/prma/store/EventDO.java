package com.caibowen.prma.store;

import java.io.Serializable;

/**
 * data object representing event in the DB
 *
 * @author BowenCai
 * @since 23-10-2014.
 */
public class EventDO implements Serializable {

    private static final long serialVersionUID = -8809863102710979910L;

//    private static final serializationID = ??

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
    public int callerSkId;

    /**
     * indicate properties, exception
     */
    public byte flag;

    /**
     * formatted
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

    public int getCallerSkId() {
        return callerSkId;
    }

    public void setCallerSkId(int callerSkId) {
        this.callerSkId = callerSkId;
    }

    public byte getFlag() {
        return flag;
    }

    public void setFlag(byte flag) {
        this.flag = flag;
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

        EventDO po = (EventDO) o;

        if (callerSkId != po.callerSkId) return false;
        if (flag != po.flag) return false;
        if (id != po.id) return false;
        if (level != po.level) return false;
        if (loggerId != po.loggerId) return false;
        if (threadId != po.threadId) return false;
        if (timeCreated != po.timeCreated) return false;
        if (!message.equals(po.message)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (timeCreated ^ (timeCreated >>> 32));
        result = 31 * result + (int) level;
        result = 31 * result + loggerId;
        result = 31 * result + threadId;
        result = 31 * result + callerSkId;
        result = 31 * result + (int) flag;
        result = 31 * result + message.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "EventPO{" +
                "id=" + id +
                ", timeCreated=" + timeCreated +
                ", level=" + level +
                ", loggerId=" + loggerId +
                ", threadId=" + threadId +
                ", callerSkId=" + callerSkId +
                ", flag=" + flag +
                ", message='" + message + '\'' +
                '}';
    }
}
