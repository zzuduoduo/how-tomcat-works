package com.zzuduoduo.chapter5.connector.http;

/**
 * http 报文头
 */
public final class HttpHeader {
    public static final int INITIAL_NAME_SIZE=32;
    public static final int INITIAL_VALUE_SIZE=64;
    public static final int MAX_NAME_SIZE=128;
    public static final int MAX_VALUE_SIZE=4096;

    public char[] name;
    public int nameEnd;
    public char[] value;
    public int valueEnd;
    protected int hashCode = 0;

    public HttpHeader(char[] name, int nameEnd, char[] value, int valueEnd){
        this.name = name;
        this.nameEnd = nameEnd;
        this.value = value;
        this.valueEnd = valueEnd;
    }

    public HttpHeader(String name, String value){
        this(name.toCharArray(),name.length(), value.toCharArray(), value.length());
    }

    public HttpHeader(){
        this(new char[INITIAL_NAME_SIZE],0, new char[INITIAL_VALUE_SIZE],0);
    }

    public void recycle(){
        nameEnd = 0;
        valueEnd = 0;
        hashCode = 0;
    }

    /**
     * 报文头是否相等
     * @param buf
     * @param end
     * @return
     */
    public boolean equals(char[] buf, int end){
        if(nameEnd!=end){
            return false;
        }
        for (int i = 0;i<end;i++){
            if(name[i] != buf[i])
                return false;
        }
        return true;
    }
    public boolean equals(String buf){
        return equals(buf.toCharArray(), buf.length());
    }
    public boolean equals(char[] buf){
        return equals(buf, buf.length);
    }

    /**
     * 报文头值是否一致
     * @param buf
     * @param end
     * @return
     */
    public boolean valueEquals(char[] buf, int end){
        if(valueEnd!=end){
            return false;
        }
        for (int i = 0;i<end;i++){
            if(value[i] != buf[i])
                return false;
        }
        return true;
    }
    public boolean valueEquals(String str){
        return valueEquals(str.toCharArray(), str.length());
    }
    public boolean valueEquals(char[] buf){
        return valueEquals(buf, buf.length);
    }

    public boolean valueIncludes(char[] buf, int end){
        char first = buf[0];
        int pos = 0;
        while(pos < valueEnd){
            pos = valueIndexOf(first, pos);
            if(pos == -1){
                return false;
            }
            if(valueEnd - pos < end){
                return false;
            }
            for(int i = 0;i<valueEnd;i++){
                if(buf[i] != value[pos+i])
                    break;
                if(i == end -1){
                    return true;
                }
            }
            pos++;
        }
        return false;
    }

    public int valueIndexOf(char c, int start){
        for(int i=start;i<valueEnd;i++){
            if(value[i] == c){
                return i;
            }
        }
        return -1;
    }

    public boolean equals(HttpHeader header){
        return equals(header.name, header.nameEnd);
    }

    public boolean headerEquals(HttpHeader header){
        if(header == null)
            return false;
        return equals(header.name, header.nameEnd) &&
                valueEquals(header.value, header.valueEnd);
    }


    public int hashCode(){
        int h = hashCode;
        if(h==0){
            for(int i = 0;i<nameEnd;i++){
                h += h*31 + name[i];
            }
        }
        return h;
    }


}
