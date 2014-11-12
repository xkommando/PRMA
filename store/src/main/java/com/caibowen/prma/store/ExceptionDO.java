package com.caibowen.prma.store;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Arrays;

/**
 *
 * data object representing exception in the DB
 *
 * @author BowenCai
 * @since 24-10-2014.
 */
public class ExceptionDO implements Serializable {

    private static final long serialVersionUID = -8580690163122391371L;

    /**
     * hash combine of exceptName, exceptMsg and stackTraces.
     */
    public long id;

    /**
     * hashCode of exception/throwalbe name
     */
    public int exceptName;

    /**
     * hashCode of exception message
     */
    @Nullable public Integer exceptMsg;

    /**
     * id array of stack trace
     */
    @Nullable public int[] stackTraces;

    public ExceptionDO() {}

    public ExceptionDO(long id, int exceptName, Integer exceptMsg, int[] stackTraces) {
        this.id = id;
        this.exceptName = exceptName;
        this.exceptMsg = exceptMsg;
        this.stackTraces = stackTraces;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getExceptName() {
        return exceptName;
    }

    public void setExceptName(int exceptName) {
        this.exceptName = exceptName;
    }

    @Nullable
    public Integer getExceptMsg() {
        return exceptMsg;
    }

    public void setExceptMsg(@Nullable Integer exceptMsg) {
        this.exceptMsg = exceptMsg;
    }

    public int[] getStackTraces() {
        return stackTraces;
    }

    public void setStackTraces(int[] stackTraces) {
        this.stackTraces = stackTraces;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExceptionDO)) return false;

        ExceptionDO that = (ExceptionDO) o;

        if (id != that.id) return false;
        if (exceptName != that.exceptName) return false;
        if (exceptMsg != null ? !exceptMsg.equals(that.exceptMsg) : that.exceptMsg != null) return false;
        if (!Arrays.equals(stackTraces, that.stackTraces)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + exceptName;
        result = 31 * result + (exceptMsg != null ? exceptMsg.hashCode() : 0);
        result = 31 * result + (stackTraces != null ? Arrays.hashCode(stackTraces) : 0);
        return result;
    }
}
