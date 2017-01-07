package com.van.monitor.systemInfo;

import java.io.*;

/**
 * 日志文件可能会比较大，该实现的seekByPercent、seekByPattern都是从offset向后查找，
 * 如需查询offset之前的内容需reset
 * // TODO: 2016/11/17 处理并发问题
 * Created by van on 2016/11/17.
 */
public class LogFile implements SeekableFile {
    private String fileName;
    private int offset;
    private long coverdSize;
    private BufferedReader lineReader;
    private File file;
    //缓存offset的上一行
    private String lastline;

    //如为true,则刚执行完seek，next时取lastline,否则直接从buffferreader读取下一行
    private boolean nextFlag;

    //用于缓存和文件系统比对，每次从文件系统遍历，先将缓存中所有对象此字段更新为false,然后遍历到更新为true,最后
    //遍历中添加上缓存中没有的文件，遍历完成从缓存删除iteratored为false的对象
    private boolean iteratored = true;

    /**
     * @param fileName 文件全路径
     */
    public LogFile(String fileName) {
        this.fileName = fileName;
        try {
            this.file = new File(fileName);
            this.lineReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(fileName), "utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public LogFile(File file) {
        this.fileName = file.getAbsolutePath();
        this.file = file;
    }

    @Override
    public synchronized boolean reset() {
        offset = 0;
        coverdSize = 0;
        lastline = null;
        if(this.lineReader!=null){
            close();
            this.lineReader=null;
        }
        return true;
    }

    /**
     * 按照大体位置进行查找
     *
     * @param percent 0到1的小数 当前offset到结尾视作1
     * @return
     */
    @Override
    public synchronized boolean seekByPercent(double percent) {
        if (percent < 0 || percent > 1) {
            throw new IllegalArgumentException("invalid value of percent, shoud be between 0 and 1");
        }
        long expect = coverdSize + java.lang.Math.round((file.length() - coverdSize) * percent);
        try {
            while (coverdSize < expect) {
                String line = lineReader.readLine();
                offset++;
                coverdSize += (line.getBytes("utf-8").length + 1);//加一个换行符 linux下
                lastline = line;
            }
            nextFlag = true;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * seek到下一次出现pattern的位置,暂时不支持正则表达式
     *
     * @param pattern
     * @return
     */
    @Override
    public synchronized boolean seekByPattern(String pattern) {
        try {
            String line;
            while (!Thread.currentThread().isInterrupted()) {
                line = lineReader.readLine();
                if (line == null) break;
                offset++;
                coverdSize += line.getBytes("utf-8").length;
                lastline = line;
                nextFlag = true;
                if (line.contains(pattern)) {
                    break;
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * 从offset读取下一行内容
     *
     * @return
     */
    @Override
    public synchronized LogLine nextLine() {
        if (this.lineReader == null) {
            try {
                this.lineReader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(file), "utf-8"));
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        if (nextFlag) {
            nextFlag = false;
            return new LogLine(offset, lastline);
        } else try {
            String line = lineReader.readLine();
            if (line != null) {
                offset++;
                coverdSize += line.getBytes("utf-8").length;
                lastline = line;
                return new LogLine(offset, line);
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        if (this.lineReader != null) {
            try {
                lineReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        close();
    }


    public void delete() {
        close();
        if (file != null && file.exists() && file.isFile()) {
            file.delete();
        }
    }

    public boolean isIteratored() {
        return iteratored;
    }

    public void setIteratored(boolean iteratored) {
        this.iteratored = iteratored;
    }
}
