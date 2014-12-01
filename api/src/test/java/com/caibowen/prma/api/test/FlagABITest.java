package com.caibowen.prma.api.test;

import com.caibowen.prma.api.FlagABI;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class FlagABITest {

    @Test
    public void testGetFlag() throws Exception {
        List exceptLs = new ArrayList(); exceptLs.add("ddd");
        Set mkset = new TreeSet();
        Map prop = new HashMap(); prop.put("k1", "value1");

        long flag = FlagABI.build(prop, mkset, exceptLs);
        assertEquals(FlagABI.exceptionCount(flag), exceptLs.size());
        assertEquals(FlagABI.markerCount(flag), mkset.size());
        assertEquals(FlagABI.propertyCount(flag), prop.size());
    }
}