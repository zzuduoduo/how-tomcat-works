package com.zzuduoduo.chapter3.processor;

import com.zzuduoduo.chapter3.connector.http.HttpRequest;
import com.zzuduoduo.chapter3.connector.http.HttpResponse;

import java.io.IOException;

public class StaticResourceProcessor implements Processor{
    @Override
    public void process(HttpRequest request, HttpResponse response) {
        try{
            response.sendStaticResource();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
