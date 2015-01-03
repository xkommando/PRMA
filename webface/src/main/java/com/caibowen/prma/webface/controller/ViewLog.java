package com.caibowen.prma.webface.controller;

import com.caibowen.gplume.web.annotation.Controller;
import com.caibowen.gplume.web.annotation.Handle;
import com.caibowen.prma.webface.JsonResult;
import com.caibowen.prma.webface.SearchEngine;

import javax.inject.Named;

/**
 * @author BowenCai
 * @since 21/12/2014.
 */
@Controller
public class ViewLog {

    @Named
    public SearchEngine engine;

    @Handle({"/log.json"})
    public JsonResult query(HttpQuery q) {
        return new JsonResult(engine.process(q));
    }


    @Handle({"/log/detail.sjon"})
    public JsonResult otherInfo(long id, long flag) {
        return new JsonResult(engine.eventDetail(id, flag));
    }

}
