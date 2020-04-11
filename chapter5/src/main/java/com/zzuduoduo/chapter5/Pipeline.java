package com.zzuduoduo.chapter5;

public interface Pipeline {
    Valve getBasic();
    void setBasic();
    void addValve(Valve valve);
    Valve[] getValves();
}
