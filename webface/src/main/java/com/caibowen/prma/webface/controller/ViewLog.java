package com.caibowen.prma.webface.controller;

import com.caibowen.gplume.web.RequestContext;
import com.caibowen.gplume.web.annotation.Controller;
import com.caibowen.gplume.web.annotation.Handle;
import com.caibowen.gplume.web.annotation.ReqParam;
import com.caibowen.prma.webface.JsonResult;
import com.caibowen.prma.webface.SearchEngine;
import gplume.scala.jdbc.DB;
import scala.beans.BeanProperty;

import javax.inject.Inject;

/**
 * @author BowenCai
 * @since 21/12/2014.
 */
@Controller("/ajax/log")
public class ViewLog {

    @Inject
    public SearchEngine engine;

//    @Handle({"/log.json"})
//    public JsonResult query(HttpQuery q) {
//        return q == null ? JsonResult.invalidParameter(): new JsonResult( new SearchEngine(null)._test());
//    }

    @Handle({"/list.json"})
    public JsonResult query(HttpQuery q, RequestContext ctx) {
        scala.collection.Seq ls = q == null ? engine.listSimple(ctx.getStrParam("p-sql")) : engine.process(q);
        return ls == null ? JsonResult.invalidParameter() : new JsonResult(ls);
    }

    class DetailQuery {
        @ReqParam(required = true)
        Long id;
        @ReqParam(required = true)
        Integer loggerName;
        @ReqParam(required = true)
        Integer threadName;
        @ReqParam(required = true)
        Long flag;
    }

    @Handle({"/detail.json"})
    public JsonResult logDetail(DetailQuery q) {
        if (q == null || q.flag < 0)
            return JsonResult.invalidParameter();
        else
            return new JsonResult(engine.detailedEvent(q.id, q.loggerName, q.threadName, q.flag));
    }

}
