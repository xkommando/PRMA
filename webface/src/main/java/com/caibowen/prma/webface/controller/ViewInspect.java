package com.caibowen.prma.webface.controller;

import com.caibowen.gplume.web.RequestContext;
import com.caibowen.gplume.web.annotation.Controller;
import com.caibowen.gplume.web.annotation.Handle;
import com.caibowen.gplume.web.annotation.ReqParam;
import com.caibowen.prma.webface.JsonResult;

/**
 * Created by Bowen Cai on 2/13/2015.
 */

@Controller("/inspect")
public class ViewInspect {

    public static class Param {
        int type;

        Long minTime;
        Long maxTime;
        Integer lowLevel;
        Integer highLevel;

        String ins2Except;
        String ins3SQL;
    }

    @Handle({"/q"})
    public JsonResult inspect(Param p, RequestContext context) {
        switch (p.type) {
            case 1: return timePeriod(p, context);
            case 2: return except(p, context);
            case 3: return query(p, context);
            default: return JsonResult.notFound();
        }
    }

    @Handle({"/time"})
    public JsonResult timePeriod(Param p, RequestContext context) {

        return JsonResult.notFound();
    }

    @Handle({"/except"})
    public JsonResult except(Param p, RequestContext context) {

        return JsonResult.notFound();
    }

    @Handle({"/query"})
    public JsonResult query(Param p, RequestContext context) {

        return JsonResult.notFound();
    }

}
