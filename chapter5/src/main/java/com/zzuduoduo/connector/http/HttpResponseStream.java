package com.zzuduoduo.connector.http;

import com.zzuduoduo.connector.ResponseStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HttpResponseStream extends ResponseStream {
    private static final int MAX_CHUNK_SIZE = 4096;
    private static final String CRLF="\r\n";
    //是否使用chunk进行传输数据
    private boolean useChunking;
    // 如果写最后一个chunk标示，则为true
    private boolean writingChunk;
    //如果不需要写数据，则为false
    private boolean writeContent;

    public HttpResponseStream(HttpResponseImpl response){
        super(response);
        checkChunking(response);
        checkHead(response);

    }
    private void checkChunking(HttpResponseImpl response){
        if (count != 0){
            return;
        }
        useChunking = (!response.isCommitted()
        && response.getContentLength() == -1
        && response.getStatus() != HttpServletResponse.SC_NOT_MODIFIED);
        if (!response.isAllowChunking() && useChunking ){
            response.setHeader("Connection", "close");
        }
        useChunking = (useChunking && !response.isCloseConnection());
        System.out.println("useChunking:"+useChunking+"isCommitted:"+response.isCommitted()
        +"getContentLength:"+response.getContentLength()+"getStatus:"+response.getStatus()+
                "isAllowChunking:"+response.isAllowChunking());
        if (useChunking){
            response.setHeader("Transer-Encoding", "chunked");
        }else if(response.isAllowChunking()){
            response.removeHeader("Transer-Encoding","chunked");
        }
    }
    protected void checkHead(HttpResponseImpl response){
        HttpServletRequest servletRequest = (HttpServletRequest) response.getRequest();
        if ("HEAD".equals(servletRequest.getMethod())){
            writeContent = false;
        }else{
            writeContent = true;
        }
    }

    @Override
    public void close() throws IOException {
        if (suspended) {
            throw new IOException("stream suspended");
        }
        if (!writeContent)
            return;
        if (useChunking){
            writingChunk = true;
            try{
                print("0\r\n\r\n");
            }finally {
                writingChunk = false;
            }
        }
        super.close();
    }

    @Override
    public void write(int b) throws IOException {
        if (suspended)
            return;
        if (!writeContent)
            return;
        if (useChunking && !writingChunk){
            writingChunk = true;
            try{
                print("1\r\n");
                super.write(b);
                println();
            }finally {
                writingChunk = false;
            }
        }else{
            super.write(b);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {

        System.out.println("write");
        if (suspended)
            return;
        if (!writeContent)
            return;
        if (useChunking && !writingChunk){
            writingChunk = true;
            try{
                println(Integer.toHexString(len));
                super.write(b, off, len);
                println();
            }finally {
                writingChunk = false;
            }
        }else{
            super.write(b, off, len);
        }
    }
}
