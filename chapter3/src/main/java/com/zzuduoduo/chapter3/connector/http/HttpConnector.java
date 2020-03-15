package com.zzuduoduo.chapter3.connector.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import static java.lang.System.exit;

public class HttpConnector implements Runnable {

    boolean stopped = false;
    public void run() {
        int port = 8080;
        ServerSocket serverSocket = null;
        try{
            serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
        }catch (IOException e){
            System.out.println("open server fail"+e);
            exit(1);
        }
        while (!stopped){
            Socket socket = null;
            try{
                socket = serverSocket.accept();
            }catch (Exception e){
                System.out.println("server accept fail");
                continue;
            }
            HttpProcessor httpProcessor = new HttpProcessor();
            httpProcessor.process(socket);
        }
    }
    public void start(){
        Thread thread = new Thread(this);
        thread.start();
    }
}
