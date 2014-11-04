package com.caibowen.prma.api.test;

import com.caibowen.prma.api.proto.Event;
import com.caibowen.prma.api.proto.StackTrace;
import com.google.protobuf.CodedOutputStream;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author BowenCai
 * @since 4-11-2014.
 */
public class ProtoTest {

    @Test
    public void s() {
        StackTrace.StackTracePO po = StackTrace.StackTracePO.newBuilder()
                .setClassName("className")
                .setFileName("fileName")
                .setFunctionName("functionName")
                .setLineNumber(5).build();

        System.out.println(po.getSerializedSize());
//        StackTrace.StackTracePO.
        System.out.println("functionNamefileNameclassName".getBytes().length);
    }
}














