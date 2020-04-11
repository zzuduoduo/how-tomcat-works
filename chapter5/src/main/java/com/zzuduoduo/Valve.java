package com.zzuduoduo;

public interface Valve {
    String getInfo();
    void invoke(Request request, Response response);
}
