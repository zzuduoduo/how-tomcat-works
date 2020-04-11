package com.zzuduoduo.connector.http;

import com.zzuduoduo.Connector;
import com.zzuduoduo.Lifecycle;
import com.zzuduoduo.LifecycleException;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpProcessor implements Lifecycle, Runnable {
    // processor对应的connector
    private HttpConnector connector;
    // 线程名
    private String threadName;
    //是否已经在其他线程里启动了
    private boolean started = false;

    // 是否有socket可以获得
    private boolean available = false;
    private Socket socket = null;

    private boolean isStopped = false;

    private HttpRequestImpl request = null;
    private HttpResponseImpl response = null;
    // keep alive http1.1默认为true
    private boolean keepAlive = false;
    private HttpRequestLine requestLine = new HttpRequestLine();

    private boolean http11 = true;
    private boolean sendAck = false;
    // processor state
    private int status = Constants.PROCESSOR_IDLE;

    public HttpProcessor(HttpConnector connector, int id){
        super();
        this.connector = connector;
        this.threadName = "httpProcessor[" + id + "]";
        this.request = (HttpRequestImpl) connector.createRequest();
        this.response = (HttpResponseImpl) connector.createResponse();
    }
    @Override
    public void start() throws LifecycleException {
        if (started){
            throw new LifecycleException("processor have started");
        }
        started = true;
        threadStart();
    }

    private void threadStart(){
        Thread thread = new Thread(this,threadName);
        thread.setDaemon(true);
        thread.start();
    }
    @Override
    public void stop() {

    }

    @Override
    public void run() {
        while (!isStopped){
            Socket socket = await();
            if (socket==null)
                continue;
            try{
                process(socket);
            }catch (Exception e){
                e.printStackTrace();
                continue;
            }
            connector.recycle(this);
        }
    }

    private void process(Socket socket){
        boolean ok = true;
        boolean finishResponse = true;
        SocketInputStream input = null;
        OutputStream output = null;

        try{
            input = new SocketInputStream(socket.getInputStream(), connector.getBufferSize());
        }catch (Exception e){
            // todo log
            ok = false;
        }
        keepAlive = true;
        int i = 0;
        while(!isStopped && ok && keepAlive){
            System.out.println("times:"+ ++i);
            finishResponse = true;
            try{
                request.setInput(input);
                request.setResponse(response);
                output = socket.getOutputStream();
                response.setOutput(output);
                response.setRequest(request);
            }catch(Exception e){
                e.printStackTrace();
            }
            try{
                if (ok){
                    parseConnection(socket);
                    parseRequest(input);
                    // todo 使用request facade
                    if (!request.getProtocol().startsWith("HTTP/0")){
                        parseHeaders(input);
                    }
                    if (http11){
                        ackRequest(output);
                        if (connector.isAllowChunking()){
                            response.setAllowChunking(true);
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                ok = false;
                //todo 这里要处理异常
            }
            // container处理请求
            try{
                //todo 设置报文头时间
                if (ok){
                    connector.getContainer().invoke(request, response);
                }
            }catch(Exception e){
                e.printStackTrace();
                ok = false;
                // 发送错误信息报文头
            }
            if(finishResponse){
                try{
                    response.finishResponse();
                }catch (IOException e){
                    ok = false;
                }

                try{
                    request.finishRequest();
                }catch(IOException e){
                    ok = false;
                }

                try{
                    if (output != null){
                        output.flush();
                    }
                }catch (IOException e){
                    ok = false;
                }
                if ("close".equals(response.getHeader("Connection"))){
                    keepAlive = false;
                }
                status = Constants.PROCESSOR_IDLE;
                // 复原request 和 response
                request.recycle();
                response.recycle();
            }
        }
        try{
            shutdownInput(input);
            socket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    protected void shutdownInput(InputStream inputStream){
        try{
            int available = inputStream.available();
            if (available > 0){
                inputStream.skip(available);
            }
        }catch (IOException e){
            ;
        }
    }
    private void ackRequest(OutputStream outputStream){
        //todo ackrequest
    }
    private void parseHeaders(SocketInputStream input) throws IOException, ServletException{
        //todo parseheaders,这里还是使用第三章的，需要重写
        while (true){
            HttpHeader header = new HttpHeader();
            input.readHeader(header);

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

    private void parseConnection(Socket socket){
        request.setSocket(socket);
    }

    private void parseRequest(SocketInputStream input) throws IOException, ServletException {
        input.readRequestLine(requestLine);
        String method = new String(requestLine.method,0,requestLine.methodEnd);
        String uri = null;
        String protocol = new String(requestLine.protocol,0,requestLine.protocolEnd);
        if (protocol.length() == 0){
            protocol = "HTTP/0.9";
        }
        if (protocol.equals("HTTP/1.1")){
            http11 = true;
            sendAck = false;
        }else{
            http11 = false;
            sendAck = false;
            keepAlive = false;
        }
        if (method.length() < 1){
            throw new ServletException("method error");
        }else if (requestLine.uriEnd < 1){
            throw new ServletException("uri error");
        }
        int question = requestLine.indexOf("?");
        if (question >= 0){
            request.setQueryString(new String(requestLine.uri,question+1,
                    requestLine.uriEnd - question - 1));
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
        // todo 解析请求路径上的sessionid
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
    private synchronized Socket await(){
        while (!available){
            try{
                wait();
            }catch (InterruptedException e){

            }
        }
        Socket socket = this.socket;
        available = false;
        notifyAll();
        return socket;
    }


    /**
     * 这个调用还在connector线程里，需要让processor的线程来接手socket进行处理，这样才能并发处理socket请求，这里是多线程的关键
     * 就是线程间如何同步的问题
     * @param socket
     */
    public synchronized void assign(Socket socket){
        while (available){
            try{
                wait();
            }catch (InterruptedException e){

            }
        }
        this.socket = socket;
        available = true;
        notifyAll();
    }
}
