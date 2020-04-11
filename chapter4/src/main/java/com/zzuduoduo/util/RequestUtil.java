package com.zzuduoduo.util;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public final class RequestUtil {
    public static void parseParameters(Map map, String data, String encoding) throws UnsupportedEncodingException {
        if (data != null && data.length() > 0){
            byte[] bytes = data.getBytes(encoding);
            parseParameters(map, bytes, encoding);
        }
    }
    public static void parseParameters(Map map, byte[] data, String encoding) throws UnsupportedEncodingException{
        if (data != null && data.length > 0){
            int pos = 0;
            int ox = 0;
            int ix = 0;
            String key = null;
            String value = null;
            while(ix < data.length){
                byte c = data[ix++];
                switch ((char)c){
                    case '&':
                        value = new String(data, 0,ox,encoding);
                        if (key != null){
                            map.put(key, value);
                            key = null;
                        }
                        ox = 0;
                        break;
                    case '=':
                        key = new String(data, 0, ox,encoding);
                        ox = 0;
                        break;
                    case '+':
                        // 这里如果是utf-16编码就有问题 todo
                        data[ox++] = ' ';
                        break;
                    case '%':
                        data[ox++] = (byte)((convertHexDigit(data[ix++]) << 4) +
                                convertHexDigit(data[ix++]));
                        break;
                    default:
                        data[ox++] = c;
                }
            }
            if (key != null){
                value = new String(data,0,ox, encoding);
                map.put(key, value);
            }
        }
    }

    // todo 不太明白
    private static byte convertHexDigit(byte b){
        if ((b >= '0') && (b <= '9')) return (byte)(b - '0');
        if ((b >= 'a') && (b <= 'f')) return (byte)(b - 'a' + 10);
        if ((b >= 'A') && (b <= 'F')) return (byte)(b - 'A' + 10);
        return 0;
    }
}
