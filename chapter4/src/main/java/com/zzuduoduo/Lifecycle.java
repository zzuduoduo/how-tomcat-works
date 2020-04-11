package com.zzuduoduo;

public interface Lifecycle {
    void start() throws LifecycleException;
    void stop();
}
