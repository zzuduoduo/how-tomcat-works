package com.zzuduoduo.chapter3.connector.http;

import java.io.OutputStream;
import java.net.Socket;

public class HttpProcessor {
    private HttpRequest request;
    public void process(Socket socket){
        SocketInputStream inputStream = null;
        OutputStream outputStream = null;
        try{
            inputStream = new SocketInputStream(socket.getInputStream(),2048);
            request = new HttpRequest(inputStream);

        }catch (Exception e){

        }
    }
}
