package com.van.monitor.systemInfo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.van.monitor.api.LogViewerMXBean;
import com.van.monitor.util.ResponseJsonSerialization;
import com.van.monitor.util.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by van on 2016/11/16.
 */
public class SimpleLogViewer extends ResponseJsonSerialization implements LogViewerMXBean {

    private Logger log = LoggerFactory.getLogger(SimpleLogViewer.class);
    private ObjectMapper mapper = new ObjectMapper();
    private Map<String, LogFile> cache;
    //private String[] filesStr;//数据库配置的obj字符串 用';'分隔目录/文件 如：/test.log;/tmp/1.log
    private Path[] paths;


    //缓存允许的层数，防止用户直接将根目录一类的东西直接配成日志目录
    private final int maxRecursiveDepth = 3;
    private int recursiveDepth = 1;

    public SimpleLogViewer(Path... path) {
        this.paths = path;
    }


    /**
     * 每次根据配置的日志路径判断缓存是否过期,同步策略为：
     * 每次从文件系统根据配置好的日志目录遍历，
     * 遍历前先将缓存中所有对象此字段更新为false,然后遍历到更新为true,
     * 最后遍历中添加上缓存中没有的文件，遍历完成从缓存删除iteratored为false的对象
     */
    private synchronized void updateConfigIfOutdate() {
        if (this.cache == null) {
            this.cache = new HashMap<>();
        }

        //重置缓存中所有对象的遍历标志
        for (LogFile f : cache.values()) {
            f.setIteratored(false);
        }
        for (Path p : paths) {
            File f = p.toFile();
            addToCache(f);
            recursiveDepth = 1;//重置
        }

        //缓存中删除未遍历到的对象
        Iterator<String> it = cache.keySet().iterator();
        while (it.hasNext()) {
            String tmp = it.next();
            if (!cache.get(tmp).isIteratored()) {
                it.remove();
            }
        }

    }

    public void clearCache() {
        if (cache != null) {
            for (LogFile l : cache.values()) {
                l.close();
            }
            this.cache.clear();
            log.info("files closed and cache cleared");
        }
    }

    private void addToCache(File f) {
        if (!f.exists()) return;
        if (f.isFile()) {
            String key = f.getAbsolutePath();
            if (!cache.containsKey(key)) {//缓存没有则添加LogFile，其默认iteratored为true
                cache.put(key, new LogFile(f));
            } else {//缓存中有的更新为true
                cache.get(key).setIteratored(true);
            }
        } else {
            if (++recursiveDepth > maxRecursiveDepth)
                throw new IllegalStateException("file recursive depth overcome max value:" + maxRecursiveDepth);
            for (File t : f.listFiles())
                addToCache(t);
        }
    }


    /**
     * 获取日志文件列表
     *
     * @return 日志文件列表
     */
    @Override
    public String getLogFiles() {
        String response;
        try {
            updateConfigIfOutdate();
            response = getSuccResponse(cache.keySet());
        } catch (Exception e) {
            response = getResponse(500, "error when updating cache:" + e.getMessage(), null);
            log.error("error when updating cache, error msg is " + e.getMessage() + ". \n", e);
        }
        return response;
    }

    /**
     * offset重置为文件开头
     *
     * @param filename
     * @return
     */
    @Override
    public String reset(String filename) {
        cache.get(filename).reset();
        return getSuccResponse(null);
    }

    /**
     * jmx远程循环调用此方法用以下载日志文件
     * 字节数组长度为0文件结束
     *
     * @param filename
     * @return
     */
    @Override
    public byte[] downloadRecursive(String filename) {
        // TODO: 2016/11/17
        return new byte[0];
    }

    /**
     * 设置查看日志的起点 如0.25即为再调用getNext时的起点位该日志文件的1/4处
     *
     * @param percent  0到1的小数
     * @param fileName 文件全路径名
     * @return
     */
    @Override
    public String setLogOffsetByPercent(Double percent, String fileName) {

        LogFile logFile = cache.get(fileName);
        if (percent < 0.0 || percent > 1.0 || logFile == null)
            return getResponse(400, "param percent or fileName are illegal", null);
        logFile.seekByPercent(percent);
        return getSuccResponse(null);
    }

    /**
     * 定位到下一次匹配pattern的行
     *
     * @param pattern  查找的字符串如20160802
     * @param fileName 文件名
     * @return
     */
    @Override
    public String setLogOffsetByPattern(String pattern, String fileName) {
        LogFile logFile = cache.get(fileName);
        if (pattern == null || "".equals(pattern) || logFile == null)
            return getResponse(400, "param percent or fileName are illegal", null);
        logFile.seekByPattern(pattern);
        return serialize(RestResponse.getSuccResponse(null));
    }

    /**
     * 对日志中的行过滤，符合条件的将被筛掉
     * 暂未使用
     *
     * @param pattern  匹配的正则表达式
     * @param fileName 要匹配的文件名
     * @return 成功true
     */
    @Override
    public boolean setExcludeFilter(String pattern, String fileName) {
        return false;
    }

    /**
     * 对日志中的行过滤，不符合条件的将被筛掉
     * 暂未使用
     *
     * @param pattern  匹配的正则表达式
     * @param fileName 文件名
     * @return 成功true
     */
    @Override
    public boolean setIncludeFileter(String pattern, String fileName) {
        return false;
    }

    /**
     * 从filename获取接下来的len行日志（被筛掉的日志不被len包含）
     *
     * @param len      行数 未付则向文件开头数
     * @param filename 文件名
     * @return 日志中符合要求的行的列表
     */
    @Override
    public String getNext(int len, String filename) {
        String rr;
        if (filename == null) {
            rr = getResponse(400, "param filename is null", null);
            return serialize(RestResponse.getSuccResponse(rr));
        }
        LogFile file = cache.get(filename);
        if (file == null) {
            rr = getResponse(400, "pleas refresh fileList", null);
            return serialize(RestResponse.getSuccResponse(rr));
        }
        List<LogLine> ret = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            LogLine line = file.nextLine();
            if (line == null) break;
            ret.add(line);
        }
        return getSuccResponse(ret);
    }

    /**
     * 删除日志
     *
     * @param fileName
     * @return
     */
    @Override
    public String delete(String[] fileName) {
        String ret;
        if (fileName == null || fileName.length == 0)
            ret = getResponse(400, "no file is chosen", null);
        else {
            List<String> undone = new ArrayList<>();
            for (String fn : fileName) {
                try {
                    cache.get(fn).delete();
                    cache.remove(fn);
                } catch (Exception e) {
                    undone.add("{\"fileName\":\"" + fn + "\",\"msg\":\"" + e.getMessage() + "\"}");
                    log.warn("error deleting file:" + fn + "\n", e);
                }
            }
            if (undone.size() == 0) {
                ret = getSuccResponse(null);
            } else {
                ret = getResponse(500, "some files may not be deleted,please refresh the file list", undone);
            }

        }
        return ret;
    }


    @Override
    protected void finalize() throws Throwable {
        clearCache();
        super.finalize();
    }
}
