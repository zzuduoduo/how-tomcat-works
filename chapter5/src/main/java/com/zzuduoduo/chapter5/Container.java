package com.zzuduoduo.chapter5;

import java.io.IOException;

public interface Container {
    void invoke(Request request, Response response) throws IOException;
}
