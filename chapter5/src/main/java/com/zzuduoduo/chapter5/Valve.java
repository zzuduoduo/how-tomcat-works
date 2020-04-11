package com.zzuduoduo.chapter5;

public interface Valve {
    String getInfo();
    void invoke(Request request, Response response);
}
