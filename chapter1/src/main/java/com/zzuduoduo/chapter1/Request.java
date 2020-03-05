package com.zzuduoduo.chapter1;

import java.io.InputStream;

/**
 *
 */
public class Request {
    private InputStream inputStream;
    private String uri;

    public Request(InputStream inputStream){
        this.inputStream = inputStream;
    }
    public void parse(){
        StringBuffer request = new StringBuffer();
        byte[] bytes = new byte[2048];
        int b = 0;
        try{
            b = inputStream.read(bytes);
        }catch (Exception e){
            System.out.println("input stream read failure");
        }
        for(int i=0;i<b;i++){
            request.append((char)bytes[i]);
        }
        System.out.println(request.toString());
        this.uri = parseUri(request.toString());

    }
    private String parseUri(String request){
        String uri = null;
        int index1,index2;
        index1 = request.indexOf(" ");
        if(index1 > -1){
            index2 = request.indexOf(" ", index1+1);
            uri = request.substring(index1+1, index2);
        }
        System.out.println(uri);
        return uri;
    }
    public String getUri(){
        return uri;
    }
}
