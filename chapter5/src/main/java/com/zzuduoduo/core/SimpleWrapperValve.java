package com.zzuduoduo.core;

import com.zzuduoduo.*;

import javax.xml.bind.ValidationEvent;

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
