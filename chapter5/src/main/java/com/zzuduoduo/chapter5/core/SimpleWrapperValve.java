package com.zzuduoduo.chapter5.core;

import com.zzuduoduo.chapter5.*;

public class SimpleWrapperValve implements Valve, Contained {
    @Override
    public Container getContainer() {
        return null;
    }

    @Override
    public void setContainer(Container container) {

    }

    @Override
    public String getInfo() {
        return null;
    }

    @Override
    public void invoke(Request request, Response response) {

    }
}
