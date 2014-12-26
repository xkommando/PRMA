package com.caibowen.prma.webface.controller;

import com.caibowen.gplume.web.annotation.ReqParam;

/**
 *
 *
 * @author BowenCai
 * @since 21/12/2014.
 */
public class HttpQuery {

    @ReqParam(required=true)
    Integer minTime;
    @ReqParam
    Integer maxTime;

    @ReqParam
    Integer lowLevel;
    @ReqParam
    Integer highLevel;

    @ReqParam
    String loggerName;

    @ReqParam
    String threadName;

    @ReqParam
    String message;

    @ReqParam
    boolean fuzzyQuery;

    @ReqParam
    boolean exceptionOnly;

}
