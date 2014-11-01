package com.caibowen.prma.store.test;

import com.caibowen.prma.store.filter.StrPrefixFilter;
import org.junit.Test;

/**
 * @author BowenCai
 * @since 30-10-2014.
 */
public class FilterTest {


    @Test
    public void t1() {
        StrPrefixFilter filter = new StrPrefixFilter();
        filter.start();
        System.out.println(filter.accept("comment"));
    }
}
