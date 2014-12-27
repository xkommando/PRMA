package com.caibowen.prma.webface.controller;

import com.caibowen.gplume.web.annotation.ReqParam;

/**
 *
 *
 * @author BowenCai
 * @since 21/12/2014.
 */
public class HttpQuery {

    @ReqParam(required=true, defaultVal = "0")
    public long minTime;

    @ReqParam(required=true, defaultVal = "9223372036854775807")
    public long maxTime;

    @ReqParam(required=true, defaultVal = "DEBUG") // DEBUG
    public String lowLevel;

    @ReqParam(required=true, defaultVal = "FATAL") // FATAL
    public String highLevel;

    @ReqParam
    public String loggerName;

    @ReqParam
    public String threadName;

    @ReqParam
    public String message;

    @ReqParam(required = true, defaultVal = "false")
    public boolean fuzzyQuery;

    @ReqParam(required = true, defaultVal = "false")
    public boolean exceptionOnly;

}
