package com.caibowne.prma.api.java.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author BowenCai
 * @since 17/12/2014.
 */
public class ExceptionVO implements Serializable{

    private long id;
    private String name;
    private String message;
    private List<StackTraceElement> stackTraces;

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

    public List<StackTraceElement> getStackTraces() {
        return stackTraces;
    }

    public void setStackTraces(List<StackTraceElement> stackTraces) {
        this.stackTraces = stackTraces;
    }
}
