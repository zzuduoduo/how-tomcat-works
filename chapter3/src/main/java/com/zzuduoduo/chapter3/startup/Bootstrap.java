package com.zzuduoduo.chapter3.startup;

import com.zzuduoduo.chapter3.connector.http.HttpConnector;

public final class Bootstrap {

    public static void main(String[] args){
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.start();
    }
}
