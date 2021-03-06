package com.zzuduoduo.chapter5;

import java.io.IOException;

public interface Request {
    void setConnector(Connector connector);
    Connector getConnector();
    void setProtocol(String protocol);
    void finishRequest() throws IOException;
    void recycle();
}
