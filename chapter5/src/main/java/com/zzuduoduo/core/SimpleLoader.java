package com.zzuduoduo.core;

import com.zzuduoduo.Container;
import com.zzuduoduo.Loader;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

public class SimpleLoader implements Loader {
    public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot/chapter3";

    private ClassLoader loader;
    private Container container;
    public SimpleLoader() {
        try{
            URL[] urls = new URL[1];
            URLStreamHandler urlStreamHandler = null;
            File classPath = new File(WEB_ROOT);
            String repository = (new URL("file",null,classPath.getCanonicalPath()+File.separator)).toString();
            System.out.println(repository);
            //urls[0] = new URL(null,repository,urlStreamHandler);
            urls[0] = new URL(null,repository, urlStreamHandler);
            loader = new URLClassLoader(urls);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public ClassLoader getClassLoader() {
        return loader;
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }
}
