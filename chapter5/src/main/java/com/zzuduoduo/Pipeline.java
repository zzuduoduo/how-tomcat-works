package com.zzuduoduo;

public interface Pipeline {
    Valve getBasic();
    void setBasic();
    void addValve(Valve valve);
    Valve[] getValves();
}
