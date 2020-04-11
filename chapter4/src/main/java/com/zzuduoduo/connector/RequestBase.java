package com.zzuduoduo.connector;

import com.zzuduoduo.Connector;
import com.zzuduoduo.Request;
import com.zzuduoduo.Response;

import javax.servlet.ServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public abstract class RequestBase implements ServletRequest, Request {

    protected InputStream input = null;
    protected Connector connector = null;
    protected Response response = null;
    protected Socket socket = null;
    protected String protocol = null;
    protected int contentLength = -1;
    protected String contentType = null;

    @Override
    public Connector getConnector() {
        return connector;
    }

    @Override
    public void setConnector(Connector connector) {
        this.connector = connector;
    }

    public InputStream getInput() {
        return input;
    }

    public void setInput(InputStream input) {
        this.input = input;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    @Override
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Override
    public void finishRequest() throws IOException {
        // todo finishRequest

    }

    @Override
    public void recycle(){
        //todo recycle
    }

    @Override
    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
        // todo 设置char encoding
    }

}
