package com.zzuduoduo.chapter5;

public interface Loader {
    ClassLoader getClassLoader();
    Container getContainer();
    void setContainer(Container container);
}
