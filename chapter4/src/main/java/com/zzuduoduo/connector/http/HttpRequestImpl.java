package com.zzuduoduo.connector.http;

import com.zzuduoduo.connector.HttpRequestBase;

/**
 * 和connector一起配合使用的request类
 */
public class HttpRequestImpl extends HttpRequestBase {
    @Override
    public void recycle(){
        super.recycle();
        //todo recycle
    }
}
