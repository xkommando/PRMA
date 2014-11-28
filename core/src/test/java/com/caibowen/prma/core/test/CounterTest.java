package com.caibowen.prma.core.test;

import com.caibowen.prma.core.FreqCounter;
import org.junit.Test;

public class CounterTest {

    FreqCounter counter = new FreqCounter();

    @Test
    public void t1() throws Throwable {
        counter.setPeriod(1.0);
        counter.setBufferSize(1024);

        int i = 0;
        while (i++ < 10000) {
            counter.count(2);
            Thread.sleep(1);
        }
        System.out.println(counter.freq());
        Thread.sleep(500);
        System.out.println(counter.freqToNow());

        while (i-- > 0) {
            counter.count();
            Thread.sleep(2);
        }
        System.out.println(counter.freq());
        Thread.sleep(1500);
        System.out.println(counter.freqToNow());
    }
}


























