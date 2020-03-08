package com.zzuduoduo.chapter2;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import java.io.*;
import java.util.Locale;

import static java.lang.System.exit;
import static java.lang.System.out;

/**
 * 获取request的uri，然后读取静态文件流，然后通过outputstream返回给client
 */
public class Response implements ServletResponse {
    private OutputStream outputStream;
    private Request request;
    private PrintWriter printWriter;
    public Response(OutputStream outputStream, Request request){
        this.outputStream = outputStream;
        this.request = request;
    }

    public String getCharacterEncoding() {
        return null;
    }

    public String getContentType() {
        return null;
    }

    public ServletOutputStream getOutputStream() throws IOException {
        return (ServletOutputStream) outputStream;
    }

    public OutputStream getOutputStream1(){
        return outputStream;
    }


    public PrintWriter getWriter() throws IOException {
        printWriter = new PrintWriter(outputStream,true);
        return printWriter;
    }

    public void setCharacterEncoding(String charset) {

    }

    public void setContentLength(int len) {

    }

    public void setContentLengthLong(long len) {

    }

    public void setContentType(String type) {

    }

    public void setBufferSize(int size) {

    }

    public int getBufferSize() {
        return 0;
    }

    public void flushBuffer() throws IOException {

    }

    public void resetBuffer() {

    }

    public boolean isCommitted() {
        return false;
    }

    public void reset() {

    }

    public void setLocale(Locale loc) {

    }

    public Locale getLocale() {
        return null;
    }
}
