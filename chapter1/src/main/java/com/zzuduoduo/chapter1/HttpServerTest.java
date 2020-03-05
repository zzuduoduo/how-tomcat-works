package com.zzuduoduo.chapter1;

public class HttpServerTest {
    public static void main(String[] args){
        HttpServer httpServer = new HttpServer(8080, 1, "127.0.0.1");
        httpServer.await();
    }
}
