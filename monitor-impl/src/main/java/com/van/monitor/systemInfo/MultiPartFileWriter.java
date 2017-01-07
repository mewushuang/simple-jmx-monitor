package com.van.monitor.systemInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 简单起见，目前文件上传是一次性（上传文件一般不会过大）写入
 * Created by van on 2017/1/5.
 */
public class MultiPartFileWriter extends MultiPartFile {

    private FileOutputStream outputStream;

    public MultiPartFileWriter(File file) throws FileNotFoundException {
        super(file);
        outputStream= new FileOutputStream(file);
        super.setCachedStream(outputStream);
    }

    public void write(byte[] data) throws IOException {
        outputStream.write(data);
    }
}
