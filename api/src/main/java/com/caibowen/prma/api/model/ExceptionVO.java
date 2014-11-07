package com.caibowen.prma.api.model;


import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Arrays;
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
    public StackTraceElement[] stackTraces;


    public ExceptionVO(){}

    public ExceptionVO(String exceptionName, String exceptionMessage, StackTraceElement[] stackTraces) {


        this.id = calculateID(exceptionName, exceptionMessage, stackTraces);
        this.exceptionName = exceptionName;
        this.exceptionMessage = exceptionMessage;
        this.stackTraces = stackTraces;
    }

    public static final long calculateID(String exceptionName, String exceptionMessage, StackTraceElement[] stackTraces) {

        long expID =  (long) exceptionName.hashCode() << 32 | exceptionMessage.hashCode() & 0xFFFFFFFFL;
        final long kMul = 0x9ddfea08eb382d69L;
        for (StackTraceElement st : stackTraces) {
            long _s = st.hashCode();
            long _a = ((expID) ^ _s ) * kMul;
            _a ^= _a >> 47;
            expID = (_s ^ _a) * kMul;
            expID ^= expID >> 47;
            expID *= kMul;
        }
        return expID;
    }

    public ExceptionVO(long id, String exceptionName, String exceptionMessage, StackTraceElement[] stackTraces) {
        this.id = id;
        this.exceptionName = exceptionName;
        this.exceptionMessage = exceptionMessage;
        this.stackTraces = stackTraces;
    }

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

    public StackTraceElement[] getStackTraces() {
        return stackTraces;
    }

    public void setStackTraces(StackTraceElement[] stackTraces) {
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
        if (!Arrays.equals(stackTraces, exception.stackTraces)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + exceptionName.hashCode();
        result = 31 * result + (exceptionMessage != null ? exceptionMessage.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(stackTraces);
        return result;
    }

    @Override
    public String toString() {
        return "Exception{" +
                "id=" + id +
                ", exceptionName='" + exceptionName + '\'' +
                ", exceptionMessage='" + exceptionMessage + '\'' +
                ", stackTraces=" + Arrays.toString(stackTraces) +
                '}';
    }
}
