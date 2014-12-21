package com.caibowen.prma.api.java.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author BowenCai
 * @since 17/12/2014.
 */
public class ExceptionVO implements Serializable{

    private long id;
    // non-null
    private String name;
    // nullable
    private String message;
    // nullable
    private List<StackTraceVO> stackTraces;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<StackTraceVO> getStackTraces() {
        return stackTraces;
    }

    public void setStackTraces(List<StackTraceVO> stackTraces) {
        this.stackTraces = stackTraces;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExceptionVO)) return false;

        ExceptionVO that = (ExceptionVO) o;

        if (id != that.id) return false;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        if (!name.equals(that.name)) return false;
        if (stackTraces != null ? !stackTraces.equals(that.stackTraces) : that.stackTraces != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + name.hashCode();
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (stackTraces != null ? stackTraces.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "com.caibowen.prma.api.java.model.ExceptionVO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", message='" + message + '\'' +
                ", stackTraces=" + stackTraces +
                '}';
    }
}
