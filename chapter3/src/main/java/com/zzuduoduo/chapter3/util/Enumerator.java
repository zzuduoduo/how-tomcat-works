package com.zzuduoduo.chapter3.util;

import java.util.*;

public final class Enumerator implements Enumeration {
    private Iterator iterator = null;

    public Enumerator(Iterator iterator){
        this.iterator = iterator;
    }

    public Enumerator(Collection collection){
        this(collection.iterator());
    }
    public Enumerator(Map map){
        this(map.values().iterator());
    }

    @Override
    public boolean hasMoreElements() {
        return this.iterator.hasNext();
    }

    @Override
    public Object nextElement() throws NoSuchElementException {
        return iterator.next();
    }
}
