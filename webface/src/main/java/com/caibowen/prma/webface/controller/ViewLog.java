package com.caibowen.prma.webface.controller;

import com.caibowen.gplume.web.annotation.Controller;
import com.caibowen.gplume.web.annotation.Handle;
import com.caibowen.prma.webface.JsonResult;
import com.caibowen.prma.webface.SearchEngine;

/**
 * @author BowenCai
 * @since 21/12/2014.
 */
@Controller("/log")
public class ViewLog {

    SearchEngine engine;

    @Handle({"/q"})
    public JsonResult query(HttpQuery q) {

//        String json = engine.process(q);
        return new JsonResult<String>(null);
    }

}
