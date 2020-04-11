package com.zzuduoduo;

import java.io.IOException;

public interface Container {
    void invoke(Request request, Response response) throws IOException;
}
