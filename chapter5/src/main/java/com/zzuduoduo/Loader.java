package com.zzuduoduo;

public interface Loader {
    ClassLoader getClassLoader();
    Container getContainer();
    void setContainer(Container container);
}
