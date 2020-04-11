package com.zzuduoduo.core;

import com.zzuduoduo.Container;
import com.zzuduoduo.Request;
import com.zzuduoduo.Response;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

public class SimpleContainer implements Container {
    public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot/chapter3";

    public SimpleContainer() {
    }

    @Override
    public void invoke(Request request, Response response) throws IOException {

        String uri = ((HttpServletRequest)request).getRequestURI();
        String servletName = uri.substring(uri.lastIndexOf("/")+1);
        URLClassLoader loader = null;
        File classPath = new File(WEB_ROOT);
        System.out.println(WEB_ROOT);

        try{
            URL[] urls = new URL[1];
            URLStreamHandler urlStreamHandler = null;
            String repository = (new URL("file",null,classPath.getCanonicalPath()+File.separator)).toString();
            System.out.println(repository);
            //urls[0] = new URL(null,repository,urlStreamHandler);
            urls[0] = new URL(null,repository, urlStreamHandler);
            loader = new URLClassLoader(urls);
        }catch (Exception e){
            System.out.println(e);
        }
        Class myClass = null;
        Servlet servlet;
        try{
            servletName = "com.zzuduoduo.chapter3.servlet."+servletName;
            System.out.println(servletName);

            myClass = loader.loadClass(servletName);
            //myClass = Class.forName(servletName, true, loader);
            servlet = (Servlet)myClass.newInstance();
            servlet.service((HttpServletRequest)request, (HttpServletResponse)response);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
