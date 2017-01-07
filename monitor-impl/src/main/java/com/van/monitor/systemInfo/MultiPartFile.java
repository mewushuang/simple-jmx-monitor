package com.van.monitor.systemInfo;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 * Created by van on 2017/1/5.
 */
public class MultiPartFile {
    protected File file;
    protected Closeable cachedStream;

    public MultiPartFile(File file) {
        this.file = file;
    }

    public void close(){
        try {
            cachedStream.close();
        } catch (IOException e) {
        }
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Closeable getCachedStream() {
        return cachedStream;
    }

    public void setCachedStream(Closeable cachedStream) {
        this.cachedStream = cachedStream;
    }
}
