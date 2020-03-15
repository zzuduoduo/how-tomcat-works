package com.zzuduoduo.chapter3.connector.http;

import java.io.IOException;
import java.io.InputStream;

/**
 * 目的是提高读取http报文头的效率,主要是增加了缓冲
 */
public class SocketInputStream extends InputStream {

    protected InputStream is;
    protected byte[] buffer;

    public SocketInputStream(InputStream is, int bufferSize){
        this.is = is;
        buffer = new byte[bufferSize];
    }


    @Override
    public int read(byte[] b) throws IOException {
        return super.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return super.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return super.skip(n);
    }

    @Override
    public int available() throws IOException {
        return super.available();
    }

    @Override
    public void close() throws IOException {
        super.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        super.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        super.reset();
    }

    @Override
    public boolean markSupported() {
        return super.markSupported();
    }

    @Override
    public int read() throws IOException {
        return 0;
    }
}
