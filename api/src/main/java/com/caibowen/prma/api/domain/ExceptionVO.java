package com.caibowen.prma.api.domain;


import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;

/**
 * @author BowenCai
 * @since 26-10-2014.
 */
public class ExceptionVO implements Serializable {

    private static final long serialVersionUID = 8087093751948611040L;

    public long id;

    public String exceptionName;

    @Nullable public String exceptionMessage;

    /**
     * ordered
     */
    public List<StackTraceElement> stackTraces;

//    public

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getExceptionName() {
        return exceptionName;
    }

    public void setExceptionName(String exceptionName) {
        this.exceptionName = exceptionName;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public List<StackTraceElement> getStackTraces() {
        return stackTraces;
    }

    public void setStackTraces(List<StackTraceElement> stackTraces) {
        this.stackTraces = stackTraces;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExceptionVO)) return false;

        ExceptionVO exception = (ExceptionVO) o;

        if (id != exception.id) return false;
        if (exceptionMessage != null ? !exceptionMessage.equals(exception.exceptionMessage) : exception.exceptionMessage != null)
            return false;
        if (!exceptionName.equals(exception.exceptionName)) return false;
        if (!stackTraces.equals(exception.stackTraces)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + exceptionName.hashCode();
        result = 31 * result + (exceptionMessage != null ? exceptionMessage.hashCode() : 0);
        result = 31 * result + stackTraces.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Exception{" +
                "id=" + id +
                ", exceptionName='" + exceptionName + '\'' +
                ", exceptionMessage='" + exceptionMessage + '\'' +
                ", stackTraces=" + stackTraces +
                '}';
    }
}
