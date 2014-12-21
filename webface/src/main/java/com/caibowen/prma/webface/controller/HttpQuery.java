package com.caibowen.prma.webface.controller;

import com.caibowen.gplume.web.annotation.ReqParam;

/**
 *
 *
 * @author BowenCai
 * @since 21/12/2014.
 */
public class HttpQuery {

    @ReqParam(defaultVal = "0")
    int offset;
    @ReqParam(defaultVal = "200")
    int limit;

    @ReqParam
    Integer minTime;
    @ReqParam
    Integer maxTime;

    @ReqParam
    Integer lowLevel;
    @ReqParam
    Integer maxLevel;


    @ReqParam
    Boolean hasException;

}
