package com.zzuduoduo.chapter5.connector.http;

import com.zzuduoduo.chapter5.*;
import com.zzuduoduo.chapter5.net.DefaultServerSocketFactory;
import com.zzuduoduo.chapter5.net.ServerSocketFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Stack;

public class HttpConnector implements Connector, Lifecycle,Runnable {

    private Container container;
    // server 绑定的地址，如果为null，则绑定此机器上的所有的ip地址
    private String address;
    // 绑定的端口号
    private int port = 8080;
    // 创建serverSocket工厂
    private ServerSocketFactory factory;
    private int connectionTimeout = Constants.DEFAULT_CONNECTION_TIMEOUT;
    // server最多接受的客户端请求，超过这个数量直接拒绝
    private int acceptCount = 10;
    private ServerSocket serverSocket;
    private boolean initialized = false;
    // connect线程是否已经启动
    private boolean start = false;
    // 线程是否已经停止
    private boolean stopped = false;
    // 设置tcp是否延迟
    private boolean tcpNoDelay = true;

    // processor已有数量
    private int curProcessors = 0;
    // 最小的processor数量
    private int minProcessors = 5;
    // 最大的processor数量
    private int maxProcessors = 20;

    //后台绑定此connector的线程，守护线程，处理客户端连接请求
    private Thread thread = null;
    //后台守护进程的线程名字
    private String threadName = null;
    private Stack<HttpProcessor> processors = new Stack();

    private int bufferSize = 2048;
    // http1.1允许返回报文成块发送 todo,暂时改为false
    private boolean allowChunking = false;

    public boolean isAllowChunking() {
        return allowChunking;
    }

    public void setAllowChunking(boolean allowChunking) {
        this.allowChunking = allowChunking;
    }

    @Override
    public void start() {
        // todo lifecycle暂时缺失,本章暂时不实现
        threadName = "HttpConnector["+port+"]";
        threadStart();
        while (curProcessors < minProcessors){
            if (maxProcessors >0 && curProcessors >= maxProcessors)
                break;
            HttpProcessor processor = newProcessor();
            recycle(processor);
        }
    }

    public void recycle(HttpProcessor processor){
        processors.push(processor);
    }

    private void threadStart(){
        thread = new Thread(this,threadName);
        thread.setDaemon(true);
        thread.start();
    }


    @Override
    public void stop() {

    }

    @Override
    public void run(){
        while (!stopped){
            Socket socket = null;
            try{
                socket = serverSocket.accept();
                if (connectionTimeout > 0){
                    socket.setSoTimeout(connectionTimeout);
                }
                // todo 可以专门研究下这里tcp delay
                socket.setTcpNoDelay(tcpNoDelay);
            }catch (IOException e){
                // todo 异常处理
            }
            HttpProcessor processor = createProcessor();
            if (processor == null){
                try{
                    socket.close();
                }catch (IOException e){

                }
                continue;
            }
            processor.assign(socket);

        }
    }

    private HttpProcessor createProcessor(){
        synchronized (processors){
            if (processors.size()>0){
                return processors.pop();
            }
            if (maxProcessors > 0 && curProcessors < maxProcessors){
                // processor自己会recycle
                return newProcessor();
            }else {
                if (maxProcessors >0){
                    return newProcessor();
                }else{
                    return null;
                }
            }
        }
    }

    private HttpProcessor newProcessor(){
        // todo
        HttpProcessor processor = new HttpProcessor(this, curProcessors++);
        if (processor instanceof Lifecycle){
            try{
                processor.start();
            }catch (LifecycleException e){
                // stack支持null，所以每次要检查下
                return null;
            }
        }
        return processor;
    }
    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public void initialize() throws LifecycleException {
        if (initialized){
            throw new LifecycleException("initialized");
        }
        initialized = true;
        try{
            serverSocket = open();
        }catch (IOException e){
            throw new LifecycleException(e.getMessage(),e.getCause());
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ServerSocketFactory getFactory() {
        // 创建单例模式
        if (this.factory == null){
            synchronized (this){
                this.factory = new DefaultServerSocketFactory();
            }
        }
        return this.factory;
    }

    public void setFactory(ServerSocketFactory factory) {
        this.factory = factory;
    }

    private ServerSocket open() throws IOException{
        ServerSocketFactory factory = getFactory();
        if (address == null){
            try{
                return factory.createSocket(port,acceptCount);
            }catch (IOException e){
                throw e;
            }
        }
        try{
            InetAddress is = InetAddress.getByName(address);
            return factory.createSocket(port,acceptCount, is);
        }catch (IOException e){
            try{
                return factory.createSocket(port, acceptCount);
            }catch (IOException ioe){
                throw ioe;
            }
        }
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public Request createRequest(){
        HttpRequestImpl request = new HttpRequestImpl();
        request.setConnector(this);
        return request;
    }
    public Response createResponse(){
        HttpResponseImpl response = new HttpResponseImpl();
        response.setConnector(this);
        return response;
    }
}
