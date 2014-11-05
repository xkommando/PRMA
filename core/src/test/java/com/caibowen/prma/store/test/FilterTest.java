package com.caibowen.prma.store.test;

import com.caibowen.prma.core.filter.StrFilter;
import org.junit.Test;

/**
 * @author BowenCai
 * @since 30-10-2014.
 */
public class FilterTest {


    @Test
    public void t1() {
        StrFilter filter = new StrFilter();
        filter.start();
        System.out.println(filter.accept("comment"));
    }
}
