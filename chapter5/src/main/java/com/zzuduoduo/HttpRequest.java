package com.zzuduoduo;

public interface HttpRequest extends Request{
    public void setMethod(String method);
    public void setRequestURI(String uri);
}
