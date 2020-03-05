package com.zzuduoduo.chapter1;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import static java.lang.System.exit;
import static java.lang.System.out;

/**
 * 获取request的uri，然后读取静态文件流，然后通过outputstream返回给client
 */
public class Response {
    private OutputStream outputStream;
    private Request request;
    public Response(OutputStream outputStream, Request request){
        this.outputStream = outputStream;
        this.request = request;
    }
    private final int BUFFERSIZE = 2048;
    public void sendStaticResource(){
        int ch = 0;
        byte[] bytes = new byte[BUFFERSIZE];
        String staticParentResourceUri =System.getProperty("user.dir") + File.separator + HttpServer.STATIC_RESOURCE;
        System.out.println(staticParentResourceUri);
        File file = new File(staticParentResourceUri, request.getUri());
        FileInputStream fileInputStream = null;
        if (file.exists()){
            try{
                fileInputStream = new FileInputStream(file);
                ch = fileInputStream.read(bytes, 0, BUFFERSIZE);
                /**
                 * 需要有返回报文头，标示http版本号，否则默认版本号是0.9，现代浏览器不再支持
                 */
                String headers = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/html\r\n" +
                        "\r\n";
                outputStream.write(headers.getBytes());
                /***/
                while (ch!=-1){
                    outputStream.write(bytes, 0, ch);
                    ch = fileInputStream.read(bytes, 0, BUFFERSIZE);
                }

                System.out.println("io success");
            }catch(Exception e){
                System.out.println("io exception");
                exit(1);
            }finally {

            }
        }else{
            String error = "HTTP/1.1 404 File Not Found\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Content-Length: 23\r\n" +
                    "\r\n" +
                    "<h1>File Not Found</h1>";
            try{
                outputStream.write(error.getBytes());
            }catch (IOException e){
                System.out.println("io exception");
                exit(1);
            }
        }
    }

}
