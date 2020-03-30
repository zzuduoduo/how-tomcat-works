package com.zzuduoduo.chapter3.processor;

import com.zzuduoduo.chapter3.connector.http.*;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

public class ServletProcessor implements Processor{
    @Override
    public void process(HttpRequest request, HttpResponse response) {

        String uri = request.getRequestURI();
        String servletName = uri.substring(uri.lastIndexOf("/")+1);
        URLClassLoader loader = null;
        File classPath = new File(Constants.WEB_ROOT);
        System.out.println(Constants.WEB_ROOT);

        try{
            URL[] urls = new URL[1];
            URLStreamHandler urlStreamHandler = null;
            String repository = (new URL("file",null,classPath.getCanonicalPath()+File.separator)).toString();
            System.out.println(repository);
            //urls[0] = new URL(null,repository,urlStreamHandler);
            urls[0] = new URL("file",null,classPath.getCanonicalPath()+File.separator);
            loader = new URLClassLoader(urls);
        }catch (Exception e){
            System.out.println(e);
        }
        Class myClass = null;
        Servlet servlet;
        try{
            System.out.println(servletName);
            servletName = "com.zzuduoduo.chapter3.servlet."+servletName;
            myClass = loader.loadClass(servletName);
            //myClass = Class.forName(servletName, true, loader);
            servlet = (Servlet)myClass.newInstance();
            HttpRequestFacade requestFacade = new HttpRequestFacade(request);
            HttpResponseFacade responseFacade = new HttpResponseFacade(response);
            servlet.service(requestFacade, responseFacade);
            response.finishResponse();
        }catch(Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
