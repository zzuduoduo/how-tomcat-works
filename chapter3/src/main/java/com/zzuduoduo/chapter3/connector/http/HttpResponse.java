package com.zzuduoduo.chapter3.connector.http;

import com.zzuduoduo.chapter3.ResponseStream;
import com.zzuduoduo.chapter3.ResponseWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;

import static java.lang.System.exit;

public class HttpResponse implements HttpServletResponse {
    private final static int BUFFER_SIZE = 1024;
    private OutputStream outputStream;
    private HttpRequest request;
    private PrintWriter writer;
    protected String encoding = null;

    protected HashMap header = new HashMap();
    protected byte[] buffer = new byte[BUFFER_SIZE];
    protected int bufferCount = 0;
    /* 实际写入的内容 */
    protected int contentCount = 0;
    protected String message = getStatusMessage(HttpServletResponse.SC_OK);
    protected int status = HttpServletResponse.SC_OK;
    protected boolean committed = false;

    public HttpResponse(OutputStream outputStream){
        this.outputStream = outputStream;
    }

    public HttpRequest getRequest() {
        return request;
    }

    public void finishResponse(){
        //sendHeaders();
        if (writer != null){
            writer.flush();
            writer.close();
        }
    }

    public void setRequest(HttpRequest request) {
        this.request = request;
    }

    public void sendStaticResource() throws IOException{
        int ch = 0;
        byte[] bytes = new byte[BUFFER_SIZE];
        String staticParentResourceUri = Constants.WEB_ROOT;
        System.out.println(staticParentResourceUri);
        File file = new File(staticParentResourceUri, request.getRequestURI());
        FileInputStream fileInputStream = null;
        if (file.exists()){
            try{
                fileInputStream = new FileInputStream(file);
                ch = fileInputStream.read(bytes, 0, BUFFER_SIZE);
                /**
                 * 需要有返回报文头，标示http版本号，否则默认版本号是0.9，现代浏览器不再支持
                 */
                String headers = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/html\r\n" +
                        "\r\n";
                outputStream.write(headers.getBytes());
                /***/
                while (ch!=-1){
                    outputStream.write(bytes, 0, ch);
                    ch = fileInputStream.read(bytes, 0, BUFFER_SIZE);
                }

                System.out.println("io success");
            }catch(Exception e){
                System.out.println("io exception");
                e.printStackTrace();
                exit(1);
            }finally {
                if(fileInputStream!=null){
                    fileInputStream.close();
                }
            }
        }else{
            String error = "HTTP/1.1 404 File Not Found\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Content-Length: 23\r\n" +
                    "\r\n" +
                    "<h1>File Not Found</h1>";
            try{
                outputStream.write(error.getBytes());
            }catch (IOException e){
                System.out.println("io exception");
                exit(1);
            }
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
        ArrayList<String> values = new ArrayList<>();
        values.add(value);
        synchronized (header){
            header.put(name, values);
        }

    }

    @Override
    public void addHeader(String name, String value) {

    }

    @Override
    public void setIntHeader(String name, int value) {

    }

    @Override
    public void addIntHeader(String name, int value) {

    }

    @Override
    public void setStatus(int sc) {

    }

    @Override
    public void setStatus(int sc, String sm) {

    }

    @Override
    public int getStatus() {
        return 0;
    }

    @Override
    public String getHeader(String name) {
        return null;
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return null;
    }

    @Override
    public Collection<String> getHeaderNames() {
        return null;
    }

    @Override
    public String getCharacterEncoding() {
        if(encoding == null){
            return "UTF-8";
        }
        return encoding;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        ResponseStream stream = new ResponseStream(this);
        stream.setCommit(true);
        OutputStreamWriter osw = new OutputStreamWriter(stream, getCharacterEncoding());
        writer = new ResponseWriter(osw);
        //writer = new PrintWriter(osw, true);
        return writer;
    }

    @Override
    public void setCharacterEncoding(String charset) {

    }

    @Override
    public void setContentLength(int len) {

    }

    @Override
    public void setContentLengthLong(long len) {

    }

    @Override
    public void setContentType(String type) {

    }

    @Override
    public void setBufferSize(int size) {

    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {
        if(bufferCount > 0){
            try{
                outputStream.write(buffer, 0, bufferCount);
            }finally {
                bufferCount=0;
            }
        }
    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public boolean isCommitted() {
        return committed;
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

    public void write(int b) throws IOException {
        if (bufferCount >= buffer.length)
            flushBuffer();
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

    protected void sendHeaders(){
        if (isCommitted())
            return;
        OutputStreamWriter osw = null;
        try{
            osw = new OutputStreamWriter(getStream(), getCharacterEncoding());
        }catch (UnsupportedEncodingException e){
            osw = new OutputStreamWriter(getStream());
        }
        PrintWriter outputWriter = new PrintWriter(osw);
        outputWriter.print(request.getProtocol());
        outputWriter.print(" ");
        outputWriter.print(status);
        outputWriter.print(" ");
        outputWriter.print(message);
        outputWriter.print("\r\n");
        if (getContentType()!=null){
            outputWriter.print("Content-Type: "+getContentType()+"\r\n");
        }
        outputWriter.flush();
        committed = true;
    }
    public OutputStream getStream(){
        return this.outputStream;
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


}
