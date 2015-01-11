package com.caibowen.prma.webface.controller;

import com.caibowen.gplume.web.RequestContext;
import com.caibowen.gplume.web.annotation.Controller;
import com.caibowen.gplume.web.annotation.Handle;
import com.caibowen.gplume.web.annotation.ReqParam;
import com.caibowen.prma.webface.JsonResult;
import com.caibowen.prma.webface.SearchEngine;
import gplume.scala.jdbc.DB;
import scala.beans.BeanProperty;

/**
 * @author BowenCai
 * @since 21/12/2014.
 */
@Controller("/log")
public class ViewLog {


//    @Inject
    public SearchEngine engine;

//    @Handle({"/log.json"})
//    public JsonResult query(HttpQuery q) {
//        return q == null ? JsonResult.invalidParameter(): new JsonResult( new SearchEngine(null)._test());
//    }
    @Handle({"/list.json"})
    public JsonResult query(HttpQuery q) {
        return q == null ? JsonResult.invalidParameter(): new JsonResult(engine.process(q));
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
        if (q == null || q.flag < 0) {
            return JsonResult.invalidParameter();
        }
        return new JsonResult(engine.detailedEvent(q.id,q.loggerName, q.threadName, q.flag));
    }

}
