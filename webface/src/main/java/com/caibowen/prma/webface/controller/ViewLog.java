package com.caibowen.prma.webface.controller;

import com.caibowen.gplume.web.RequestContext;
import com.caibowen.gplume.web.annotation.Controller;
import com.caibowen.gplume.web.annotation.Handle;
import com.caibowen.gplume.web.annotation.ReqParam;
import com.caibowen.prma.webface.JsonResult;
import com.caibowen.prma.webface.SearchEngine;

/**
 * @author BowenCai
 * @since 21/12/2014.
 */
@Controller
public class ViewLog {

//    @Inject
    public SearchEngine engine;

//    @Handle({"/log.json"})
//    public JsonResult query(HttpQuery q) {
//        return q == null ? JsonResult.invalidParameter(): new JsonResult( new SearchEngine(null)._test());
//    }
    @Handle({"/log/list.json"})
    public JsonResult query(HttpQuery q) {
        return q == null ? JsonResult.invalidParameter(): new JsonResult(engine.process(q));
    }

    class DetailQuery {
        @ReqParam(required = true)
        Integer loggerName;
        @ReqParam(required = true)
        Long id;
        @ReqParam(required = true)
        Long flag;
    }

    @Handle({"/log/detail.json"})
    public JsonResult logDetail(DetailQuery q) {
        if (q == null || q.flag < 0) {
            return JsonResult.invalidParameter();
        }
        return new JsonResult(engine.detailedEvent(q.loggerName, q.id, q.flag));
    }

}
