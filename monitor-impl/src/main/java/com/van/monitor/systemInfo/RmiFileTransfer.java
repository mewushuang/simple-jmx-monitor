package com.van.monitor.systemInfo;

import com.van.monitor.util.ResponseJsonSerialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 暂定为只允许单用户同步下载
 * 下载过程：
 * 1.请求request id askForDownload()
 * 2.根据id多次调用downloadRecursive()获取数据
 * 3.返回空或空数组时调用endDownload释放资源，获取结果
 * <p>
 * 上传:
 * uploadFile()
 * <p>
 * Created by van on 2017/1/5.
 */
public class RmiFileTransfer extends ResponseJsonSerialization {
    private final Logger logger = LoggerFactory.getLogger(RmiFileTransfer.class);

    private Random random = new Random();
    private Map<String, MultiPartFileReader> readerCache;
    private ScheduledExecutorService clearThread;
    private int timeout = 30;//30分钟未

    public RmiFileTransfer() {
        this.readerCache = new ConcurrentHashMap<>();
        clearThread = Executors.newSingleThreadScheduledExecutor();
        clearThread.schedule(new Runnable() {
            @Override
            public void run() {
                logger.info("clearing abandon download request");
                long current = System.currentTimeMillis();
                for (Map.Entry<String, MultiPartFileReader> e : readerCache.entrySet()) {
                    if (e.getValue() != null && e.getValue().getGenerateTime() > current - timeout * 60 * 1000) {
                    } else {
                        MultiPartFileReader deleted =readerCache.remove(e.getKey());
                        logger.info("cleared abandon download request on file[" + deleted.getFile().getAbsolutePath() + "] ");
                    }
                }
            }
        }, 30, TimeUnit.MINUTES);
    }

    /**
     * @param requestId null表示第一次请求下载某个文件,此时将返回该json
     * @return
     */
    public byte[] downloadRecursive(String requestId) {
        if (readerCache.get(requestId) != null) {
            return readerCache.get(requestId).read();
        }
        return null;
    }

    public String askForDownload(File file) {
        String id = System.currentTimeMillis() + "" + random.nextInt(9999999);
        try {
            MultiPartFileReader reader = new MultiPartFileReader(file);
            readerCache.put(id, reader);
        } catch (FileNotFoundException e) {
            return getResponse(400, "file not exist[" + file.getAbsolutePath() + "]", null);
        }
        return getSuccResponse(id);
    }

    public String endDownload(String requestId) {
        MultiPartFileReader reader = readerCache.get(requestId);
        if (reader != null) {
            Exception e = reader.getException();
            logger.info("end downloading file["+reader.getFile().getAbsolutePath().toString()+"], releasing resource");
            reader.close();
            readerCache.remove(requestId);
            if (e == null) {
                return getEmptySuccResponse();
            } else {
                return getResponse(500, "error reading from file:" + e.getMessage(), e);
            }
        }
        return getResponse(500, "request id[" + requestId + "] of downloading does not exist", null);

    }


    public String uploadFile(File file, byte[] data) {
        MultiPartFileWriter des = null;
        try {
            des = new MultiPartFileWriter(file);
            des.write(data);
            return getEmptySuccResponse();
        } catch (IOException e) {
            return getResponse(500, "error uploading file[" + file.getAbsolutePath() + "]:" + e.getMessage(), e);
        } finally {
            if (des != null) {
                des.close();
            }
        }
    }

    public void close(){
        logger.info("clearing download cache if exists");
        synchronized (clearThread){
            for(MultiPartFile file : readerCache.values()){
                file.close();
            }
        }
        clearThread.shutdownNow();
    }

}
