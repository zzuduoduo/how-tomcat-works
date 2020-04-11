package com.zzuduoduo.core;

import com.zzuduoduo.Pipeline;
import com.zzuduoduo.Valve;

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
