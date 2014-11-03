package com.caibowen.prma.logger.test;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.*;

/**
 * @author BowenCai
 * @since 3-11-2014.
 */
public class ThreadPoolRuning {

    ExecutorService exe = Executors.newFixedThreadPool(30);
    ArrayBlockingQueue<Character> arr = new ArrayBlockingQueue<Character>(10);


    @Test
    public void tt() throws Throwable {
//        exe.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Character c = arr.take();
//                    System.out.println(Thread.currentThread().getName() + " eating '" + c + "'");
////                    Thread.sleep(500);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        System.out.println(">> enter sth");
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        String buf = r.readLine();
        System.out.println(buf);
        while (true) {
            buf = r.readLine();
//            buf.chars().forEach((c) -> {try {
//                arr.put((char)c);
//            }catch (Exception e){e.printStackTrace();}
//            });
            System.out.println("<< " + buf);
            for (Character c : buf.toCharArray())
                arr.put(c);
        }
    }



//    @Test
    public void t1() throws Exception {
        exe.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        System.out.println(Thread.currentThread().getName());
                        Thread.sleep(500L);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
//        exe.awaitTermination(10, TimeUnit.SECONDS);
//        exe.shutdown();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        System.out.println(Thread.currentThread().getName());
                        Thread.sleep(500L);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }}).start();
    }
}
