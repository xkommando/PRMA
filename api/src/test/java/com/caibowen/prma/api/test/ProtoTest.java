package com.caibowen.prma.api.test;

import org.junit.Test;

/**
* @author BowenCai
* @since 4-11-2014.
*/
public class ProtoTest {

    @Test
    public void s() throws Throwable {
        com.caibowen.prma.api.proto.StackTrace.StackTracePO po = com.caibowen.prma.api.proto.StackTrace.StackTracePO.newBuilder()
                .setClassName("className")
                .setFileName("fileName")
                .setFunctionName("functionName")
                .setLineNumber(5).build();
//        ByteArrayOutputStream bo = new ByteArrayOutputStream(1024);
//        ObjectOutputStream oo = new ObjectOutputStream(bo);
//        oo.writeObject(st);
//        oo.flush();
//        oo.close();

//        StackTrace st = new StackTrace("className", "functionName", "fileName", 5);
//        MessagePack pack = new MessagePack();
//        pack.register(StackTrace.class);
//        byte[] mp = pack.write(st);

        System.out.println("str: " + "functionNamefileNameclassName".getBytes().length);

//        System.out.println("java " + bo.size());
        System.out.println("proto: " + po.getSerializedSize());
//        System.out.println("mp: " + mp.length);
//str: 29
//        java 185
//        proto: 37
//        mp: 34
    }
}














