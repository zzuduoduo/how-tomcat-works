package com.zzuduoduo.chapter5;

import javax.servlet.ServletException;
import java.io.IOException;

public interface ValveContext {
    String getInfo();
    void invokeNext(Request request, Response response) throws IOException, ServletException;
}
