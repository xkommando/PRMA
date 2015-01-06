package com.caibowen.prma.webface.controller;

import com.caibowen.gplume.web.RequestContext;
import com.caibowen.gplume.web.annotation.Controller;
import com.caibowen.gplume.web.annotation.Handle;
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
    @Handle({"/log.json"})
    public JsonResult query(HttpQuery q) {
        return q == null ? JsonResult.invalidParameter(): new JsonResult(engine.process(q));
    }

    @Handle({"/log/detail.json"})
    public JsonResult logDetail(RequestContext ctx) {
        Long id = ctx.getLongParam("id");
        Long flag = ctx.getLongParam("flag");
        if (id == null || flag == null || flag < 0) {
            return JsonResult.invalidParameter();
        }
        return new JsonResult(engine.detailedEvent(id, flag));
    }

}
