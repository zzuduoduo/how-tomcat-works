package com.zzuduoduo.chapter5;

import java.io.IOException;

public interface Response {
    public void setConnector(Connector connector);
    public Connector getConnector();
    void recycle();
    void finishResponse() throws IOException;
}
