package com.zzuduoduo.chapter1;

public class HttpServerTest {
    public static void main(String[] args){
        HttpServer httpServer = new HttpServer(8080, 1, "127.0.0.1");
        httpServer.await();
    }

    public void threadTest(){
        TestThread testThread = new TestThread();
        Thread thread = new Thread(testThread);
        thread.setDaemon(true);
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
}
