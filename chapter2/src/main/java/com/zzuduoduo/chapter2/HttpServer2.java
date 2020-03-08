package com.zzuduoduo.chapter2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import static java.lang.System.exit;

public class HttpServer2 {
    private final int port;
    private final int backlog;
    private final String ip;
    private final String SHUTDOWN="/shutdown";
    public static final String STATIC_RESOURCE = "webroot";

    public HttpServer2(int port, int backlog, String ip){
        this.port = port;
        this.backlog = backlog;
        this.ip = ip;
    }
    public void await(){
        InetAddress inetAddress = null;
        ServerSocket serverSocket = null;
        try{
            inetAddress = InetAddress.getByName(ip);
        }catch (UnknownHostException e){
            System.out.println("unkownhost");
            exit(1);
        }
        try{
            serverSocket = new ServerSocket(port, backlog, inetAddress);
        }catch (IOException e){
            System.out.println("open server fail"+e);
            exit(1);
        }
        boolean block = false;
        while (!block){
            Socket socket;
            OutputStream outputStream;
            InputStream inputStream;
            try{
                socket = serverSocket.accept();
                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();

                Request requset = new Request(inputStream);
                requset.parse();

                Response response = new Response(outputStream, requset);
                if(requset.getUri().startsWith("/servlet")){
                    Processor processor = new ServletProcessor2();
                    processor.process(requset, response);
                }else{
                    Processor processor = new StaticResourceProcessor();
                    processor.process(requset, response);
                }
                socket.close();
                block = SHUTDOWN.equals(requset.getUri());
            }catch (Exception e){
                System.out.println("server accept fail");
            }

        }

    }
}
