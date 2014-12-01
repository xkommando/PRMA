package com.caibowen.prma.core.test;

import com.caibowen.prma.core.filter.PartialStrFilter;
import com.caibowen.prma.core.filter.StrFilter;
import org.junit.Test;

/**
 * @author BowenCai
 * @since 30-10-2014.
 */
public class FilterTest {

    @Test
    public void t1() {
//        E:\Dev\prma\ws\PRMA\store\src\test\resource\ignored_classes.txt
        StrFilter filter = new PartialStrFilter("classpath:ignored_classes.txt");
        filter.start();
        System.out.println(filter.accept("comment"));
        System.out.println(filter.accept("org.junit.runners.model.FrameworkMethod"));
    }
}
