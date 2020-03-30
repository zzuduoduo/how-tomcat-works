package com.zzuduoduo.chapter3.processor;

import com.zzuduoduo.chapter3.connector.http.HttpRequest;
import com.zzuduoduo.chapter3.connector.http.HttpResponse;

public interface Processor {
    public void process(HttpRequest request, HttpResponse response);
}
