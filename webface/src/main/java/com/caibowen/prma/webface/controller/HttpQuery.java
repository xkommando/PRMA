package com.caibowen.prma.webface.controller;

import com.caibowen.gplume.web.annotation.ReqParam;
import scala.collection.mutable.*;

import java.lang.reflect.*;

/**
 *
 *
 * @author BowenCai
 * @since 21/12/2014.
 */
public class HttpQuery {

//
//    public static void main(String[] args) throws Throwable {
//
//        Class s = Class.forName("scala.collection.mutable.ArrayBuffer");
//        for (Method m : s.getDeclaredMethods()) {
//            System.out.println(m.getName());
//        }
//    }

    @ReqParam(required=true)
    public long minTime;

    @ReqParam(required=true)
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
