package com.zzuduoduo.chapter5.core;

import com.zzuduoduo.chapter5.Pipeline;
import com.zzuduoduo.chapter5.Valve;

public class SimplePipeline implements Pipeline {
    @Override
    public Valve getBasic() {
        return null;
    }

    @Override
    public void setBasic() {

    }

    @Override
    public void addValve(Valve valve) {

    }

    @Override
    public Valve[] getValves() {
        return new Valve[0];
    }
}
