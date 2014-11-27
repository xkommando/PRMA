package com.caibowen.prma.monitor.notify;

import com.caibowen.prma.api.model.EventVO;
import com.caibowen.prma.core.LifeCycle;

import java.util.Map;

/**
 * @author BowenCai
 * @since 26/11/2014.
 */
public class HttpNotiy implements Notifier, LifeCycle{

    private String name = "PRMA.DefaultHTTPNotify";

    protected Map<String, String> httpHeaders;

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isStarted() {
        return false;
    }

    @Override
    public boolean notify(EventVO vo) {
        return false;
    }


    protected void reconnect() {

    }


    public Map<String, String> getHttpHeaders() {
        return httpHeaders;
    }

    public void setHttpHeaders(Map<String, String> httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }



}
