package com.caibowen.prma.webface.controller;

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

    SearchEngine engine = new SearchEngine();

    @Handle({"/log.json"})
    public JsonResult query(HttpQuery q) {

//        String json = engine.process(q);
        return engine._test();
    }

}
