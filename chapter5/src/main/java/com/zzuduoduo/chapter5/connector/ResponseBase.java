package com.zzuduoduo.chapter5.connector;

import com.zzuduoduo.chapter5.Connector;
import com.zzuduoduo.chapter5.Request;
import com.zzuduoduo.chapter5.Response;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public abstract class ResponseBase implements ServletResponse, Response {
    protected Connector connector = null;
    protected OutputStream output;
    protected Request request;
    protected PrintWriter writer = null;
    protected ServletOutputStream stream = null;
    protected int bufferCount = 0;
    protected byte[] buffer = new byte[1024];
    protected boolean committed = false;
    protected int contentCount = 0;
    protected String encoding = null;
    protected int contentLength = -1;
    protected boolean included = false;
    protected String contentType;

    public boolean isCommitted() {
        return committed;
    }
    public String getContentType() {
        return contentType;
    }

    @Override
    public Connector getConnector() {
        return connector;
    }

    @Override
    public void setConnector(Connector connector) {
        this.connector = connector;
    }

    public OutputStream getOutput() {
        return output;
    }

    public void setOutput(OutputStream output) {
        this.output = output;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    @Override
    public void finishResponse() throws IOException {
        if(this.stream == null){
            return;
        }

        if (((ResponseStream) stream).isClosed()){
            return;
        }
        if (writer!=null){
            System.out.println("writer flush");
            writer.flush();
            writer.close();
        }else{
            stream.flush();
            stream.close();
        }
    }
    @Override
    public void recycle(){
        // todo recycle
        bufferCount = 0;
        committed = false;
        contentCount = 0;
        encoding = null;
        output = null;
        request = null;
        stream = null;
        writer = null;
    }
    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer != null){
            return writer;
        }
        if (stream != null){
            throw new IOException("stream error");
        }
        ResponseStream newStream = (ResponseStream)createOutputStream();
        newStream.setCommit(false);
        OutputStreamWriter osw = new OutputStreamWriter(newStream, getCharacterEncoding());
        writer = new ResponseWriter(osw, newStream);
        this.stream = newStream;
        return writer;
    }
    @Override
    public String getCharacterEncoding(){
        if(encoding == null){
            return "UTF-8";
        }
        return encoding;
    }

    public ServletOutputStream createOutputStream() throws IOException {
        return new ResponseStream(this);
    }

    public void write(int b) throws IOException {
        if (bufferCount >= buffer.length){
            flushBuffer();
        }
        buffer[bufferCount++] = (byte)b;
        contentCount++;
    }
    public void write(byte[] b, int off, int len) throws IOException{
        if (len == 0){
            return;
        }
        if (len <= (buffer.length - bufferCount)){
            System.arraycopy(b, off, buffer, bufferCount, len);
            bufferCount += len;
            contentCount += len;
            return;
        }
        flushBuffer();
        int iterations = len / buffer.length;
        int leftoverStart = iterations * buffer.length;
        int leftoverLen = len - leftoverStart;
        for (int i = 0; i<iterations;i++){
            write(b,off + i*buffer.length,buffer.length);
        }
        if (leftoverLen > 0)
            write(b,off+leftoverStart, leftoverLen);
    }
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }


    @Override
    public void flushBuffer() throws IOException{
        committed = true;
        if (bufferCount > 0){
            try{
                output.write(buffer, 0, bufferCount);
            }finally {
                bufferCount = 0;
            }
        }
    }
    @Override
    public ServletOutputStream getOutputStream() throws IOException{
        if (writer != null){
            throw new IOException("response write");
        }
        if (stream == null){
            stream = createOutputStream();
        }
        ((ResponseStream)stream).setCommit(true);
        return stream;
    }

    public int getContentLength() {
        return contentLength;
    }

    @Override
    public void setContentLength(int contentLength) {
        if(isCommitted())
            return;
        if (included)
            return;
        this.contentLength = contentLength;
    }

    @Override
    public void setContentType(String type) {
        if (isCommitted())
            return;
        if (included)
            return;
        this.contentType = type;
        //todo
    }
}

