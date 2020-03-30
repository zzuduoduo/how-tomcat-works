package com.zzuduoduo.chapter3.connector.http;

import com.zzuduoduo.chapter3.processor.Processor;
import com.zzuduoduo.chapter3.processor.ServletProcessor;
import com.zzuduoduo.chapter3.processor.StaticResourceProcessor;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class HttpProcessor {
    private HttpRequest request;
    private HttpResponse response;
    private HttpRequestLine requestLine = new HttpRequestLine();
    public void process(Socket socket){
        SocketInputStream inputStream = null;
        OutputStream outputStream = null;
        try{
            System.out.println("process start");
            inputStream = new SocketInputStream(socket.getInputStream(),2048);
            outputStream = socket.getOutputStream();
            request = new HttpRequest(inputStream);

            response = new HttpResponse(outputStream);
            response.setRequest(request);

            response.setHeader("Server", "simple Servlet container");
            System.out.println("process middle");

            // 解析request和header
            parseRequestLine(inputStream);
            System.out.println("process middle1");
            parseHeaders(inputStream);
            System.out.println("process middle2");

            if(request.getRequestURI().startsWith("/servlet")){
                Processor processor = new ServletProcessor();
                processor.process(request, response);
                System.out.println("process servlet");
            }else{
                Processor processor = new StaticResourceProcessor();
                processor.process(request, response);
                System.out.println("process static");

            }
            System.out.println("process end");

            socket.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 此方法主要是解析简单的报文头，比如content-length,content-type
     * @param input
     */
    private void parseHeaders(SocketInputStream input) throws IOException, ServletException{
        while (true){
            HttpHeader header = new HttpHeader();
            input.readHeader(header);
            System.out.println("parseHeader");

            if(header.nameEnd == 0 ){
                if(header.valueEnd != 0){
                    throw new ServletException("invalid header");
                }else{
                    return;
                }
            }
            String name = new String(header.name, 0, header.nameEnd);
            String value = new String(header.value, 0, header.valueEnd);
            request.addHeader(name, value);
            // todo set content-length,conteng-type,cookie
            if(name.equals("content-length")){
                int length = 0;
                try{
                    length = Integer.parseInt(value);
                }catch (NumberFormatException e){
                    throw new ServletException("content-length error");
                }
                request.setContentLength(length);
            }
            if(name.equals("content-type")){
                request.setContentType(value);
            }
        }
    }
    // 解析报文行
    private void parseRequestLine(SocketInputStream input) throws IOException, ServletException {
        input.readRequestLine(requestLine);
        String method = new String(requestLine.method,0,requestLine.methodEnd);
        String uri = null;
        String protocol = new String(requestLine.protocol,0,requestLine.protocolEnd);

        if(method.length() < 1){
            throw new ServletException("missing http method");
        }else if(requestLine.uriEnd < 1){
            throw new ServletException("missing http url");
        }
        // 解析http query参数
        int question = requestLine.indexOf("?");
        if(question >= 0){
            request.setQueryString(new String(requestLine.uri, question+1,requestLine.uriEnd-question -1));
            uri = new String(requestLine.uri,0,question);
        }else{
            request.setQueryString(null);
            uri = new String(requestLine.uri,0,requestLine.uriEnd);
        }
        // 检查一个绝对路径的uri
        if(!uri.startsWith("/")){
            int pos = uri.indexOf("://");
            if(pos != -1){
                pos = uri.indexOf('/', pos+3);
                if(pos == -1){
                    uri = "";
                }else {
                    uri = uri.substring(pos);
                }
            }
        }
        // todo 解析jsessionid
        String normalizedUri = normalize(uri);
        request.setMethod(method);
        request.setProtocol(protocol);
        if(normalizedUri != null){
            request.setRequestURI(normalizedUri);
        }else{
            throw new ServletException("Invaild uri" + uri);
        }
    }
    protected String normalize(String path){
        // todo 规则化uri
        return path;
    }
}
