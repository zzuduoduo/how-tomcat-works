package com.zzuduoduo.chapter5.connector.http;

import com.zzuduoduo.chapter5.connector.HttpResponseBase;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 和connector关联的response
 */
public class HttpResponseImpl extends HttpResponseBase {
    protected boolean allowChunking;
    protected HttpResponseStream responseStream;

    public boolean isAllowChunking() {
        return allowChunking;
    }

    public void setAllowChunking(boolean allowChunking) {
        this.allowChunking = allowChunking;
    }

    protected String getProtocol(){
        return "HTTP/1.1";
    }

    @Override
    public void finishResponse() throws IOException {
        //todo finishResponse 如果有报错，报文头connection需要是close
        if (getStatus() < SC_BAD_REQUEST){
            // 如果stream还没有初始化？
            if ((!isStreamInitialized()) && (getContentLength() == -1)
            && (getStatus() >= 200)
            && (getStatus() != SC_NOT_MODIFIED)
            && (getStatus() != SC_NO_CONTENT)){
                System.out.println();
                setContentLength(0);
            }
        }else{

            setHeader("Connection", "close");
        }
        super.finishResponse();
    }

    @Override
    public void recycle(){
        super.recycle();
        responseStream = null;
        allowChunking = false;
    }
    public boolean isCloseConnection(){
        String connectionValue = (String)getHeader("Connection");
        return (connectionValue != null && connectionValue.equals("close"));
    }
    public void removeHeader(String name, String value){
        if (isCommitted())
            return;
        if (included)
            return;
        synchronized (headers){
            ArrayList<String> values = headers.get(name);
            if (values != null && !values.isEmpty()){
                values.remove(value);
                if (value.isEmpty()){
                    headers.remove(name);
                }
            }
        }
    }

    @Override
    public ServletOutputStream createOutputStream() throws IOException {
        responseStream = new HttpResponseStream(this);
        return responseStream;
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        addHeader("Connection", "close");
        super.sendError(sc, msg);
    }
    public boolean isStreamInitialized(){
        return responseStream != null;
    }
}
