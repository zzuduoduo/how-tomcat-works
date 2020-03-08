package com.zzuduoduo.chapter2;

public class HttpServerTest {
    public static void main(String[] args){
        HttpServer2 httpServer = new HttpServer2(8080, 1, "127.0.0.1");
        httpServer.await();
    }
}
