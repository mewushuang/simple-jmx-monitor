package com.van.monitor.systemInfo;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by van on 2017/1/5.
 */
public class MultiPartFileReader extends MultiPartFile {

    private int bufferSize = 500 * 1024;
    private FileChannel fileChannel;
    private ByteBuffer buffer;
    private Exception errorReading;
    private long generateTime;

    public MultiPartFileReader(File file) throws FileNotFoundException {
        super(file);
        fileChannel = new FileInputStream(file).getChannel();
        buffer = ByteBuffer.allocate(bufferSize);
        this.generateTime=System.currentTimeMillis();
        super.setCachedStream(fileChannel);
    }

    public byte[] read() {
        buffer.clear();
        try {
            fileChannel.read(buffer);
            buffer.flip();
            if(buffer.limit()!=0){
                byte[] data=new byte[buffer.limit()];
                buffer.get(data);
                return data;
            }else return new byte[0];
        } catch (IOException e) {
            errorReading=e;
            return null;
        }
    }

    public Exception getException(){
        return errorReading;
    }

    public long getGenerateTime() {
        return generateTime;
    }
}
