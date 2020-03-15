package com.zzuduoduo.chapter3.connector.http;

/**
 * http 请求行，就是http报文第一行，比如 GET /icwork/?search=product HTTP/1.1
 */
public final class HttpRequestLine {
    public static final int INITIAL_METHOD_SIZE = 8;
    public static final int INITIAL_URI_SIZE=64;
    public static final int INITIAL_PROTOCOL_SIZE=8;
    public static final int MAX_METHOD_SIZE=1024;
    public static final int MAX_URI_SIZE=32768;
    public static final int MAX_PROTOCOL_SIZE=1024;

    public char[] method;
    public int methodEnd;
    public char[] uri;
    public int uriEnd;
    public char[] protocol;
    public int protocolEnd;

    public HttpRequestLine(){
        this(new char[INITIAL_METHOD_SIZE],INITIAL_METHOD_SIZE,
                new char[INITIAL_URI_SIZE],INITIAL_URI_SIZE,
                new char[INITIAL_PROTOCOL_SIZE],INITIAL_PROTOCOL_SIZE);
    }
    public HttpRequestLine(char[] method,int methodEnd,char[] uri,
                           int uriEnd,char[] protocol,int protocolEnd){
        this.method = method;
        this.methodEnd = methodEnd;
        this.uri = uri;
        this.uriEnd = uriEnd;
        this.protocol = protocol;
        this.protocolEnd = protocolEnd;
    }

    public void recyle(){
        methodEnd = 0;
        uriEnd = 0;
        protocolEnd = 0;
    }

    public int indexOf(char c, int start){
        for (int i = start;i<uriEnd;i++){
            if(c == uri[i]){
                return i;
            }
        }
        return -1;
    }

    public int indexOf(char[] buf, int end){
        char firstChar = buf[0];
        int pos = 0;
        while(pos < uriEnd){
            pos = indexOf(firstChar, pos);
            if(pos == -1){
                return -1;
            }
            if(uriEnd - pos < end){
                return -1;
            }
            for(int i = 0;i<end;i++){
                if(buf[i] != uri[pos+i])
                    break;
                if(i == end -1){
                    return pos;
                }
            }
            pos++;
        }
        return -1;
    }
    public int indexOf(char[] buf){
        return indexOf(buf, buf.length);
    }

    public int indexOf(String str){
        return indexOf(str.toCharArray(), str.length());
    }



}
