package com.caibowen.prma.webface.controller;

import com.caibowen.gplume.web.HttpMethod;
import com.caibowen.gplume.web.RequestContext;
import com.caibowen.gplume.web.annotation.Controller;
import com.caibowen.gplume.web.annotation.Handle;
import com.caibowen.prma.api.model.EventVO$;
import com.caibowen.prma.webface.JsonResult;
import com.caibowen.prma.webface.JsonResult$;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author BowenCai
 * @since 17/12/2014.
 */
@Controller
public class NotificationCtrl {

    @Handle({"/notice.json"})
    public void index(RequestContext context){

    }

    class _Post {

    }

    @Handle(value = {"/"}, httpMethods = {HttpMethod.GET, HttpMethod.POST})
    public JsonResult add(_Post post) {

        return JsonResult$.MODULE$.ok();
    }

}







