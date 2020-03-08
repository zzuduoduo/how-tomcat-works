package com.zzuduoduo.chapter2;

import javax.servlet.Servlet;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

public class ServletProcessor2 implements Processor {
    @Override
    public void process(Request request, Response response) {
        String uri = request.getUri();
        String servletName = uri.substring(uri.lastIndexOf("/")+1);
        URLClassLoader loader = null;
        File classPath = new File(Constants.WEB_ROOT);

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
            servletName = "com.zzuduoduo.chapter2."+servletName;
            myClass = loader.loadClass(servletName);
            //myClass = Class.forName(servletName, true, loader);
            servlet = (Servlet)myClass.newInstance();
            RequestFacade requestFacade = new RequestFacade(request);
            ResponseFacade responseFacade = new ResponseFacade(response);
            servlet.service(requestFacade, responseFacade);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e);
        }
    }
}
