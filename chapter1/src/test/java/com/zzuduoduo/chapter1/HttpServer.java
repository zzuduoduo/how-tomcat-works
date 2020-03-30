package com.zzuduoduo.chapter1;

import org.junit.Test;

import java.io.UnsupportedEncodingException;

public class HttpServer {

    @Test
    public void switchTest(){
        int a = 2;
        switch (a){
            case 1:
                System.out.println("1");
            case 2:
                System.out.println("2");
            default:
                System.out.println("3");
        }
    }

    @Test
    public void charTest(){
        String a = " ";
        try {
            byte[] b = a.getBytes("UTF-16");
            int i =0;
            for (byte c: b){
                System.out.println((char)c);
                System.out.println(i++);
            }
        }catch (Exception e){

        }
    }
}
