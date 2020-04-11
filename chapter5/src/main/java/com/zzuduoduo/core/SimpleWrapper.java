package com.zzuduoduo.core;

import com.zzuduoduo.Pipeline;
import com.zzuduoduo.Request;
import com.zzuduoduo.Response;
import com.zzuduoduo.Wrapper;

import java.io.IOException;

public class SimpleWrapper implements Wrapper, Pipeline {
    @Override
    public void invoke(Request request, Response response) throws IOException {

    }
}
