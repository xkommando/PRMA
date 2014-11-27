package com.caibowen.prma.core.test;

import com.caibowen.prma.core.filter.freq.Counter;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static org.junit.Assert.*;

public class CounterTest {



    Counter counter = new Counter();

    @Test
    public void t1() throws Throwable {
        int i = 0;
        while (i++ < 1000) {
            counter.add();
            Thread.sleep(2);
        }
        System.out.println(counter.freq());
        while (i-- > 0) {
            counter.add();
            Thread.sleep(4);
        }
        System.out.println(counter.freq());
        Thread.sleep(2000);
        System.out.println(counter.freqToNow());
    }
}


























