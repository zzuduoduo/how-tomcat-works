package com.zzuduoduo.connector;

import com.zzuduoduo.HttpResponse;
import sun.jvm.hotspot.debugger.win32.coff.WindowsNTSubsystem;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class HttpResponseBase extends ResponseBase implements HttpResponse, HttpServletResponse {
    protected HashMap<String, ArrayList<String>> headers = new HashMap();
    protected int status = SC_OK;
    protected String message = getStatusMessage(SC_OK);


    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public void setCharacterEncoding(String charset) {

    }

    @Override
    public void setContentLength(int len) {
        if (isCommitted())
            return;
        if (included)
            return;
        super.setContentLength(len);
    }

    @Override
    public void setContentLengthLong(long len) {

    }

    @Override
    public void setContentType(String type) {

        if (isCommitted())
            return;
        if (included)
            return;
        super.setContentType(type);
    }

    @Override
    public void setBufferSize(int size) {

    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void resetBuffer() {

    }


    @Override
    public void reset() {

    }

    @Override
    public void setLocale(Locale loc) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public void finishResponse() throws IOException {
        //todo 如果有报错，发送报错信息给client
        if (!isCommitted() && (stream == null) && (writer == null) &&
                (status >= SC_BAD_REQUEST) &&
                (contentType == null) &&
                (contentCount == 0)
        ){
            try {
                setContentType("text/html");
                PrintWriter writer = getWriter();
                writer.println("<html>");
                writer.println("<head>");
                writer.println("<title>Tomcat Error Report</title>");
                writer.println("<br><br>");
                writer.println("<h1>HTTP Status ");
                writer.print(status);
                writer.print(" - ");
                if (message != null)
                    writer.print(message);
                else
                    writer.print(getStatusMessage(status));
                writer.println("</h1>");
                writer.println("</body>");
                writer.println("</html>");
            } catch (IOException e) {
                throw e;
            } catch (Throwable e) {
                ;       // Just eat it
            }
        }
        sendHeaders();
        super.finishResponse();

    }
    protected String getStatusMessage(int status) {

        switch (status) {
            case SC_OK:
                return ("OK");
            case SC_ACCEPTED:
                return ("Accepted");
            case SC_BAD_GATEWAY:
                return ("Bad Gateway");
            case SC_BAD_REQUEST:
                return ("Bad Request");
            case SC_CONFLICT:
                return ("Conflict");
            case SC_CONTINUE:
                return ("Continue");
            case SC_CREATED:
                return ("Created");
            case SC_EXPECTATION_FAILED:
                return ("Expectation Failed");
            case SC_FORBIDDEN:
                return ("Forbidden");
            case SC_GATEWAY_TIMEOUT:
                return ("Gateway Timeout");
            case SC_GONE:
                return ("Gone");
            case SC_HTTP_VERSION_NOT_SUPPORTED:
                return ("HTTP Version Not Supported");
            case SC_INTERNAL_SERVER_ERROR:
                return ("Internal Server Error");
            case SC_LENGTH_REQUIRED:
                return ("Length Required");
            case SC_METHOD_NOT_ALLOWED:
                return ("Method Not Allowed");
            case SC_MOVED_PERMANENTLY:
                return ("Moved Permanently");
            case SC_MOVED_TEMPORARILY:
                return ("Moved Temporarily");
            case SC_MULTIPLE_CHOICES:
                return ("Multiple Choices");
            case SC_NO_CONTENT:
                return ("No Content");
            case SC_NON_AUTHORITATIVE_INFORMATION:
                return ("Non-Authoritative Information");
            case SC_NOT_ACCEPTABLE:
                return ("Not Acceptable");
            case SC_NOT_FOUND:
                return ("Not Found");
            case SC_NOT_IMPLEMENTED:
                return ("Not Implemented");
            case SC_NOT_MODIFIED:
                return ("Not Modified");
            case SC_PARTIAL_CONTENT:
                return ("Partial Content");
            case SC_PAYMENT_REQUIRED:
                return ("Payment Required");
            case SC_PRECONDITION_FAILED:
                return ("Precondition Failed");
            case SC_PROXY_AUTHENTICATION_REQUIRED:
                return ("Proxy Authentication Required");
            case SC_REQUEST_ENTITY_TOO_LARGE:
                return ("Request Entity Too Large");
            case SC_REQUEST_TIMEOUT:
                return ("Request Timeout");
            case SC_REQUEST_URI_TOO_LONG:
                return ("Request URI Too Long");
            case SC_REQUESTED_RANGE_NOT_SATISFIABLE:
                return ("Requested Range Not Satisfiable");
            case SC_RESET_CONTENT:
                return ("Reset Content");
            case SC_SEE_OTHER:
                return ("See Other");
            case SC_SERVICE_UNAVAILABLE:
                return ("Service Unavailable");
            case SC_SWITCHING_PROTOCOLS:
                return ("Switching Protocols");
            case SC_UNAUTHORIZED:
                return ("Unauthorized");
            case SC_UNSUPPORTED_MEDIA_TYPE:
                return ("Unsupported Media Type");
            case SC_USE_PROXY:
                return ("Use Proxy");
            case 207:       // WebDAV
                return ("Multi-Status");
            case 422:       // WebDAV
                return ("Unprocessable Entity");
            case 423:       // WebDAV
                return ("Locked");
            case 507:       // WebDAV
                return ("Insufficient Storage");
            default:
                return ("HTTP Response Status " + status);
        }

    }
    protected void sendHeaders() throws IOException{
        // todo sendHeaders,这里感觉不太理解，实践结果显示在这里再发送报文头已经晚了，但不知道tomcat源码为啥也是这么写
        if(isCommitted())
            return;
        // todo http0.9版本就不再处理
        OutputStreamWriter osr = null;
        try{
            osr = new OutputStreamWriter(getOutput(),getCharacterEncoding());
        }catch (UnsupportedEncodingException e){
            osr = new OutputStreamWriter(getOutput());
        }
        PrintWriter outputWriter = new PrintWriter(osr);
        outputWriter.print("HTTP/1.1");
        outputWriter.print(" ");
        outputWriter.print(status);
        if (message != null){
            outputWriter.print(" ");
            outputWriter.print(message);
        }
        outputWriter.print("\r\n");
        if (getContentType() != null){
            outputWriter.print("Content-Type: "+getContentType()+"\r\n");
        }
        if (getContentLength() > 0){
            outputWriter.print("Content-Length: "+getContentLength()+"\r\n");
        }
        synchronized (headers){
            Iterator<String> names = headers.keySet().iterator();
            while(names.hasNext()){
                String name = names.next();
                ArrayList<String> values = headers.get(name);
                Iterator<String> items = values.iterator();
                while(items.hasNext()){
                    String value = items.next();
                    outputWriter.print(name);
                    outputWriter.print(": ");
                    outputWriter.print(value);
                    outputWriter.print("\r\n");
                }
            }
        }
        outputWriter.print("\r\n");
        outputWriter.flush();
        committed = true;
    }


    public String getHeader(String name){
        ArrayList<String> values = null;
        //todo 为什么这里要加锁呢
        synchronized (headers){
            values = headers.get(name);
        }
        if (values != null){
            return values.get(0);
        }else{
            return null;
        }
    }

    @Override
    public void addCookie(Cookie cookie) {

    }

    @Override
    public boolean containsHeader(String name) {
        return false;
    }

    @Override
    public String encodeURL(String url) {
        return null;
    }

    @Override
    public String encodeRedirectURL(String url) {
        return null;
    }

    @Override
    public String encodeUrl(String url) {
        return null;
    }

    @Override
    public String encodeRedirectUrl(String url) {
        return null;
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {

    }

    @Override
    public void sendError(int sc) throws IOException {

    }

    @Override
    public void sendRedirect(String location) throws IOException {

    }

    @Override
    public void setDateHeader(String name, long date) {

    }

    @Override
    public void addDateHeader(String name, long date) {

    }

    @Override
    public void setHeader(String name, String value) {
        if (isCommitted())
            return;
        if (included)
            return;
        ArrayList<String> values = new ArrayList<>();
        values.add(value);
        synchronized (headers){
            headers.put(name, values);
        }

    }

    @Override
    public void addHeader(String name, String value) {
        if (isCommitted())
            return;
        if (included)
            return;
        // todo 在单线程里为什么要加锁呢
        synchronized (headers){
            ArrayList<String> values = headers.get(name);
            if (values==null){
                values = new ArrayList<>();
                headers.put(name, values);
            }
            values.add(value);
        }
    }

    @Override
    public void setIntHeader(String name, int value) {

    }

    @Override
    public void addIntHeader(String name, int value) {
        addHeader(name, ""+value);
    }

    @Override
    public void setStatus(int sc) {
        status = sc;
    }

    @Override
    public void setStatus(int sc, String sm) {
        if (included)
            return;
        this.status = sc;
        this.message = sm;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return null;
    }

    @Override
    public Collection<String> getHeaderNames() {
        return null;
    }

    /**
     * flush缓冲器，如果这是第一个output，则首先将报文头数据先发送出去，再发送报文体
     * @throws IOException
     */
    @Override
    public void flushBuffer() throws IOException {
        doFlushBuffer();
    }
    private void doFlushBuffer() throws IOException {
        if (!isCommitted())
            sendHeaders();
        super.flushBuffer();
    }
}
