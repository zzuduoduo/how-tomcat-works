package com.zzuduoduo.startup;

import com.zzuduoduo.connector.http.HttpConnector;
import com.zzuduoduo.core.SimpleContainer;

public final class Bootstrap {
    public static void main(String[] args){
        HttpConnector connector = new HttpConnector();
        SimpleContainer container = new SimpleContainer();
        connector.setContainer(container);
        try{
            connector.initialize();
            connector.start();
            System.in.read();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
