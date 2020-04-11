package com.zzuduoduo.chapter5.core;

import com.zzuduoduo.chapter5.Pipeline;
import com.zzuduoduo.chapter5.Request;
import com.zzuduoduo.chapter5.Response;
import com.zzuduoduo.chapter5.Wrapper;

import java.io.IOException;

public class SimpleWrapper implements Wrapper, Pipeline {
    @Override
    public void invoke(Request request, Response response) throws IOException {

    }
}
