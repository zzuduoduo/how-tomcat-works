package com.zzuduoduo.chapter3.connector.http;

import com.zzuduoduo.chapter3.RequestStream;
import com.zzuduoduo.chapter3.util.Enumerator;
import com.zzuduoduo.chapter3.util.RequestUtil;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.security.Principal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HttpRequest implements HttpServletRequest {

    private InputStream inputStream;
    private InetAddress inetAddress;
    private String method;
    private String protocol;
    private String requestURI;
    private String queryString;
    private String contentType;
    private int contentLength;
    protected HashMap<String, ArrayList<String>> headers = new HashMap();
    protected ServletInputStream stream = null;
    protected BufferedReader reader = null;

    protected ConcurrentHashMap<String, String> parameters = null;
    protected boolean parsed = false;

    public void addHeader(String name, String value){
        name = name.toLowerCase();
        synchronized (headers){
            ArrayList<String> values = headers.get(name);
            if(values != null){
                values.add(value);
            }else{
                values = new ArrayList<>();
                values.add(value);
                headers.put(name, values);
            }
        }
    }

    public HttpRequest(InputStream inputStream){
        this.inputStream = inputStream;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    @Override
    public String getAuthType() {
        return null;
    }

    @Override
    public Cookie[] getCookies() {
        return new Cookie[0];
    }

    @Override
    public long getDateHeader(String name) {
        return 0;
    }

    @Override
    public String getHeader(String name) {
        String lowerName = name.toLowerCase();
        ArrayList<String> arrayList = headers.get(lowerName);
        if (arrayList != null && arrayList.size() > 0){
            return arrayList.get(0);
        }
        return null;
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return null;
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return new Enumerator(headers.keySet());
    }

    @Override
    public int getIntHeader(String name) {
        return 0;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public String getPathInfo() {
        return null;
    }

    @Override
    public String getPathTranslated() {
        return null;
    }

    @Override
    public String getContextPath() {
        return null;
    }

    @Override
    public String getQueryString() {
        return queryString;
    }

    @Override
    public String getRemoteUser() {
        return null;
    }

    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public String getRequestedSessionId() {
        return null;
    }

    @Override
    public String getRequestURI() {
        return requestURI;
    }

    @Override
    public StringBuffer getRequestURL() {
        return null;
    }

    @Override
    public String getServletPath() {
        return null;
    }

    @Override
    public HttpSession getSession(boolean create) {
        return null;
    }

    @Override
    public HttpSession getSession() {
        return null;
    }

    @Override
    public String changeSessionId() {
        return null;
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    @Override
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        return false;
    }

    @Override
    public void login(String username, String password) throws ServletException {

    }

    @Override
    public void logout() throws ServletException {

    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return null;
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {
        return null;
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        return null;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return null;
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {

    }

    @Override
    public int getContentLength() {
        return contentLength;
    }

    @Override
    public long getContentLengthLong() {
        return 0;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public String getParameter(String name) {
        parseParameters();
        return (String)parameters.get(name);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        parseParameters();
        return new Enumerator(parameters.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        return new String[0];
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return null;
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    @Override
    public String getScheme() {
        return null;
    }

    @Override
    public String getServerName() {
        return null;
    }

    @Override
    public int getServerPort() {
        return 0;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return null;
    }

    @Override
    public String getRemoteAddr() {
        return null;
    }

    @Override
    public String getRemoteHost() {
        return null;
    }

    @Override
    public void setAttribute(String name, Object o) {

    }

    @Override
    public void removeAttribute(String name) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return null;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }

    @Override
    public String getRealPath(String path) {
        return null;
    }

    @Override
    public int getRemotePort() {
        return 0;
    }

    @Override
    public String getLocalName() {
        return null;
    }

    @Override
    public String getLocalAddr() {
        return null;
    }

    @Override
    public int getLocalPort() {
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return null;
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return null;
    }

    @Override
    public boolean isAsyncStarted() {
        return false;
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return null;
    }

    @Override
    public DispatcherType getDispatcherType() {
        return null;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public ServletInputStream createInputStream() throws IOException {
        return new RequestStream(this);
    }

    protected void parseParameters(){
        if (parsed)
            return;
        ConcurrentHashMap results = parameters;
        if (results == null){
            results = new ConcurrentHashMap();
        }
        String encoding = getCharacterEncoding();
        if (encoding == null)
            encoding = "UTF-8";
        String queryString = getQueryString();
        try{
            RequestUtil.parseParameters(results, queryString, encoding);
        }catch(UnsupportedEncodingException e){
            System.out.println("unsuport encoding" + e.getStackTrace());
        }
        // todo post没有解析
        parsed = true;
        parameters = results;
    }
}
