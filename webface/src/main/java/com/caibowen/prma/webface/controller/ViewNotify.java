package com.caibowen.prma.webface.controller;

import com.caibowen.gplume.web.RequestContext;
import com.caibowen.gplume.web.annotation.Controller;
import com.caibowen.gplume.web.annotation.Handle;
import com.caibowen.prma.api.model.EventVO$;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author BowenCai
 * @since 17/12/2014.
 */
@Controller
public class ViewNotify {

    @Handle({"/http-notices"})
    public void index(RequestContext context){
    }

    @Test
    public void ref() {
        for (Field f: EventVO$.class.getDeclaredFields())
            System.out.println(f);

        System.out.println("=================");
        for (Method m : EventVO$.class.getDeclaredMethods())
            System.out.println(m);
    }


}
