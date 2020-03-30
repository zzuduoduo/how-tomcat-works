package com.zzuduoduo.chapter3;

import com.zzuduoduo.chapter3.connector.http.HttpRequest;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.IOException;
import java.io.InputStream;

public class RequestStream extends ServletInputStream {

    protected InputStream stream = null;
    // 请求流是否关闭
    protected boolean closed = false;
    // 已经读了多少字节
    protected int count = 0;
    // 设置最多可以读的长度，如果为-1，则不限制
    protected int length = -1;

    public RequestStream(HttpRequest request){
        super();
        try{
            stream = request.getInputStream();
        }catch (IOException e){

        }
    }

    @Override
    public void close() throws IOException {
        if (closed)
            throw new IOException("requeststream close");
        if (length > 0){
            while (count < length){
                int b = read();
                if (b < 0){
                    break;
                }
            }
        }
        closed = true;
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {

    }

    @Override
    public int read() throws IOException {
        if (closed)
            throw new IOException("requeststream close");
        if (length >= 0 && count >= length)
            throw new IOException("request stream too long");
        int b = stream.read();
        if (b >= 0)
            count++;
        return b;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int toRead = len;
        if (length > 0){
            if (count >= length){
                return  -1;
            }
            if (count + len > length){
                toRead = length - count;
            }
        }
        int actuallyRead = super.read(b, off, toRead);
        return actuallyRead;
    }
}
