package com.zzuduoduo.chapter1;

import org.junit.Test;


public class HttpServer {

    @Test
    public void switchTest(){
        int a = 2;
        switch (a){
            case 1:
                System.out.println("1");
            case 2:
                System.out.println("2");
            default:
                System.out.println("3");
        }
    }

    @Test
    public void charTest(){
        int d = -0b10000001;
        System.out.println(d);
        String a = " ";
        try {
            byte[] b = a.getBytes("UTF-16");
            int i =0;
            for (byte c: b){
                System.out.println((char)c);
                System.out.println(i++);
            }
        }catch (Exception e){

        }
    }

    @Test
    public void threadTest(){
        TestThread testThread = new TestThread();
        Thread thread = new Thread(testThread);
        thread.start();
        System.out.println("end");
    }


    static class TestThread implements Runnable{
        @Override
        public void run() {
            int i = 0;
            try {
                while (true) {
                    System.out.println(i++);
                    Thread.sleep(1000);
                }
            }catch (Exception e){

            }
        }
    }

    @Test
    public void stringTest1(){
        String aa = "sdfsdf";
        System.out.println(System.identityHashCode(aa));
        stringTest(aa);
        System.out.println(System.identityHashCode(aa));

    }

    public void stringTest(String a){
        System.out.println(System.identityHashCode(a));
        System.out.println(a);
        a = "sss";
        System.out.println(System.identityHashCode(a));
    }
}
