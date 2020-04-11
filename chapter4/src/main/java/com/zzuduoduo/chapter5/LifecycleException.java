package com.zzuduoduo.chapter5;

public class LifecycleException extends Exception {
    protected String message = null;
    protected Throwable throwable = null;

    public LifecycleException(String message, Throwable throwable){
        super();
        this.message = message;
        this.throwable = throwable;
    }
    public LifecycleException(String message){
        this(message,null);
    }
}
