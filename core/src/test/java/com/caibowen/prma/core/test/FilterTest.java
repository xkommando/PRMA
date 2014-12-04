package com.caibowen.prma.core.test;

import com.caibowen.prma.core.filter.PartialStrFilter;
import com.caibowen.prma.core.filter.StrFilter;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author BowenCai
 * @since 30-10-2014.
 */
public class FilterTest {

    static {

        StackTraceElement[] _p = new Throwable().getStackTrace();
        for (StackTraceElement t : _p)
            System.err.println(t.getClassName());
    }
    @Test
    public void t1() {
        StackTraceElement[] _p = new Throwable().getStackTrace();
        for (StackTraceElement t : _p)
            System.err.println(t.getClassName());
//        E:\Dev\prma\ws\PRMA\store\src\test\resource\ignored_classes.txt
        StrFilter filter = new PartialStrFilter("classpath:ignored_classes.txt");
        filter.start();
        System.out.println(filter.accept("comment"));
        System.out.println(filter.accept("org.junit.runners.model.FrameworkMethod"));
    }
}
