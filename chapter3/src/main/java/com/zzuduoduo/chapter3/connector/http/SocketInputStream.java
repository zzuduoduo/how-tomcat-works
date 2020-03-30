package com.zzuduoduo.chapter3.connector.http;


import java.io.IOException;
import java.io.InputStream;

/**
 * 目的是提高读取http报文头的效率,主要是增加了缓冲
 */
public class SocketInputStream extends InputStream {

    protected InputStream is;
    protected byte[] buffer;
    // buffer 中的位置
    protected int pos;
    // buffer最后一个位置
    protected int count;

    private static final byte CR = (byte)'\r';
    private static final byte LF = (byte)'\n';
    private static final byte SP = (byte)' ';
    private static final byte COLON = (byte)':';
    private static final byte HT = (byte)'\t';
    private static final int LC_OFFSET = 'A' - 'a';





    public SocketInputStream(InputStream is, int bufferSize){
        this.is = is;
        buffer = new byte[bufferSize];
    }

    /**
     * 读取报文请求行
     * @param requestLine
     * @throws IOException
     */
    public void readRequestLine(HttpRequestLine requestLine) throws IOException {
        if (requestLine.methodEnd != 0)
            requestLine.recyle();
        int chr = 0;
        do {
            try {
                chr = read();
            } catch (IOException e) {
                chr = -1;
            }
        } while (chr == CR || chr == LF);

        if (chr == -1) {
            throw new IOException("read header error");
        }
        pos--;
        int readCount = 0;
        int maxRead = requestLine.method.length;
        int readStart = pos;
        boolean space = false;
        while(!space){

            // 读取的内容超过了method的大小
            if(readCount >= maxRead){
                if(2*maxRead <= HttpRequestLine.MAX_METHOD_SIZE){
                    char[] newBuffer = new char[2*maxRead];
                    System.arraycopy(requestLine.method, 0,newBuffer, 0, maxRead);
                    requestLine.method = newBuffer;
                    maxRead = requestLine.method.length;
                }else{
                    throw new IOException("request line too long");
                }
            }
            if(pos >= count){
                int val = read();
                if(val == -1){
                    throw new IOException("");
                }
                pos = 0;
                readStart = 0;
            }
            if(buffer[pos] == SP){
                space = true;
            }else{
                requestLine.method[readCount] = (char)buffer[pos];
                readCount++;
            }
            pos++;
        }
        requestLine.methodEnd = readCount;
        maxRead = requestLine.uri.length;
        readStart = pos;
        readCount = 0;
        space = false;

        while(!space){
            // 读取的内容超过了method的大小
            if(readCount >= maxRead){
                if(2*maxRead <= HttpRequestLine.MAX_URI_SIZE){
                    char[] newBuffer = new char[2*maxRead];
                    System.arraycopy(requestLine.uri, 0,newBuffer, 0, maxRead);
                    requestLine.uri = newBuffer;
                    maxRead = requestLine.uri.length;
                }else{
                    throw new IOException("request line too long");
                }
            }
            if(pos >= count){
                int val = read();
                if(val == -1){
                    throw new IOException("");
                }
                pos = 0;
                readStart = 0;
            }
            if(buffer[pos] == SP){
                space = true;
            }else{
                requestLine.uri[readCount] = (char)buffer[pos];
                readCount++;
            }
            pos++;
        }

        maxRead = requestLine.protocol.length;
        requestLine.uriEnd = readCount;
        readCount = 0;
        boolean eol = false;
        while(!eol){
            // 读取的内容超过了method的大小
            if(readCount >= maxRead){
                if(2*maxRead <= HttpRequestLine.MAX_PROTOCOL_SIZE){
                    char[] newBuffer = new char[2*maxRead];
                    System.arraycopy(requestLine.protocol, 0,newBuffer, 0, maxRead);
                    requestLine.protocol = newBuffer;
                    maxRead = requestLine.protocol.length;
                }else{
                    throw new IOException("request line too long");
                }
            }
            if(pos >= count){
                int val = read();
                if(val == -1){
                    throw new IOException("");
                }
                pos = 0;
                readStart = 0;
            }
            if(buffer[pos] == CR){
            }else if(buffer[pos] == LF){
                eol = true;
            }else{
                requestLine.protocol[readCount] = (char)buffer[pos];
                readCount++;
            }
            pos++;
        }
        requestLine.protocolEnd = readCount;


    }

    /**
     * 读取报文头
     * @param header
     */
    public void readHeader(HttpHeader header) throws IOException{
        if(header == null){
            return;
        }
        if(header.nameEnd!=0){
            header.recycle();
        }
        int chr = read();
        /* 遇到空行 */
        if(chr == CR || chr == LF){
            if(chr == CR){
                chr = read();
            }
            header.nameEnd = 0;
            header.valueEnd = 0;
            return;
        }else{
            pos--;
        }
        int maxRead = header.name.length;
        int readStart = pos;
        int readCount = 0;

        boolean colon = false;
        while (!colon){
            /* 读取的内容超过了name的大小 */
            if(readCount >= maxRead){
                if(2 * maxRead <= HttpHeader.MAX_NAME_SIZE){
                    char[] newBuffer = new char[2 * maxRead];
                    System.arraycopy(header.name, 0,newBuffer, 0, maxRead);
                    header.name = newBuffer;
                    maxRead = header.name.length;
                }else{
                    throw new IOException("request line too long");
                }
            }
            if(pos >= count){
                int val = read();
                if(val == -1){
                    throw new IOException("");
                }
                pos = 0;
                readStart = 0;
            }
            if(buffer[pos] == COLON){
                colon = true;
            }else{
                char val = (char)buffer[pos];
                if(val >= 'A' && val <= 'Z'){
                    val =  (char)(val - LC_OFFSET);
                }
                header.name[readCount] = val;
                readCount++;
            }
            pos++;
        }
        header.nameEnd = readCount;

        /* 读取header value，可能会跨多行*/
        maxRead = header.value.length;
        readCount = 0;

        int crPos = -2;

        boolean eol = false;
        boolean validLine = false;

        while(!validLine){
            //System.out.println("readheader 237");
            boolean space = true;
            while(space){
                if(pos >= count){
                    System.out.println("readheader 241");
                    int val = read();
                    if(val == -1){
                        throw new IOException("too long space");
                    }
                    pos = 0;
                }
                if(buffer[pos] == SP || buffer[pos]== HT){
                    pos++;
                }else{
                    space = false;
                }
                //System.out.println(space);
            }

            while (!eol){
                // 读取的内容超过了vale的大小
                if(readCount >= maxRead){
                    if(2 * maxRead <= HttpHeader.MAX_VALUE_SIZE){
                        char[] newBuffer = new char[2 * maxRead];
                        System.arraycopy(header.value, 0,newBuffer, 0, maxRead);
                        header.value = newBuffer;
                        maxRead = header.value.length;
                    }else{
                        throw new IOException("request line too long");
                    }
                }
                if(pos >= count){
                    int val = read();
                    if(val == -1){
                        throw new IOException("");
                    }
                    pos = 0;
                }
                if(buffer[pos] == CR){
                } else if(buffer[pos] == LF){
                    eol = true;
                } else {
                    int val = buffer[pos] & 0xff;
                    header.value[readCount] = (char)val;
                    readCount++;
                }
                pos++;
            }
            int nextChr = read();
            //System.out.println((char) nextChr);

            if((nextChr != SP) && (nextChr != HT)){
                validLine = true;
                pos--;
            }else{
                if(readCount >= maxRead){
                    if(2 * maxRead <= HttpHeader.MAX_VALUE_SIZE){
                        char[] newBuffer = new char[2 * maxRead];
                        System.arraycopy(header.value, 0,newBuffer, 0, maxRead);
                        header.value = newBuffer;
                        maxRead = header.value.length;
                    }else{
                        throw new IOException("request line too long");
                    }
                }
                header.value[readCount] = ' ';
                readCount++;
            }
        }
        header.valueEnd = readCount;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return super.read(b);
    }

    @Override
    public int read() throws IOException {
        if(pos >= count){
            fill();
            if(pos>count){
                return -1;
            }
        }
        // todo 这里为啥要和0xff进行求并呢

        return buffer[pos++] & 0xff;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return super.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return super.skip(n);
    }

    @Override
    public int available() throws IOException {
        return super.available();
    }

    @Override
    public void close() throws IOException {
        super.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        super.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        super.reset();
    }

    @Override
    public boolean markSupported() {
        return super.markSupported();
    }


    protected void fill() throws IOException{
        pos = 0;
        count = 0;
        int nRead = is.read(buffer, 0, buffer.length);
        if(nRead > 0)
            count = nRead;
        System.out.println("buffer"+(new String(buffer)));
    }

}
