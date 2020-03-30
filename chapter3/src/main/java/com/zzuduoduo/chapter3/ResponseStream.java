package com.zzuduoduo.chapter3;

import com.zzuduoduo.chapter3.connector.http.HttpResponse;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.IOException;

public class ResponseStream extends ServletOutputStream {
    private HttpResponse response = null;
    protected boolean closed = false;
    protected boolean commit = false;
    // 记录写入的大小
    protected int count = 0;
    // 超过了length大小，就不再写入
    protected int length = -1;
    public ResponseStream(HttpResponse response){
        super();
        this.response = response;
    }

    public boolean isCommit() {
        return commit;
    }

    public void setCommit(boolean commit) {
        this.commit = commit;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {

    }

    @Override
    public void write(int b) throws IOException {
        if(closed)
            throw new IOException("responsestream closed");
        if(length > 0 && count >= length)
            throw new IOException("responsestream count too long");
        response.write(b);
        count++;
    }

    @Override
    public void flush() throws IOException {
        if(closed){
            throw new IOException("response has close");
        }
        if(commit){
            response.flushBuffer();
        }
    }

    @Override
    public void close() throws IOException {
        if(closed)
            throw new IOException("response close");
        response.flushBuffer();
        closed = true;
    }

    @Override
    public void write(byte[] b) throws IOException {

    }
    public void write(byte[] b, int off, int len) throws IOException{
        if (closed)
            throw new IOException("response write closed");
        int actual = len;
        if (length > 0 && (count + len >= length))
            actual = length - count;
        response.write(b, off, actual);
        count += actual;
        if (actual < len)
            throw new IOException("response write too long");
    }
}