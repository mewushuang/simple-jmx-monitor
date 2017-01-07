package com.van.monitor.api;

/**
 * Created by van on 2016/11/15.
 */
public interface LogViewerMXBean {
    String BEAN_NAME="com.van.LogView.defaultName:type=logViewer";

    /**
     * 获取日志文件列表
     * @return 日志文件列表,路径相对root(程序根目录)
     */
    String getLogFiles();

    /**
     * 获取配置的可查看文件的目录
     * @return
     */
    String getParentPaths();

    /**
     * offset重置为文件开头
     * @return
     */
    String  reset(String filename);


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
     */
    byte[] downloadRecursive(String requestId);
    String startDownload(String fullFileName);
    String endDownload(String requestId);

    String uploadFile(String path,String filename,byte[] data);

    /**
     * 设置查看日志的起点 如0.25即为再调用getNext时的起点位该日志文件的1/4处
     * @param percent
     * @param fileName
     * @return
     */
    String setLogOffsetByPercent(Double percent,String fileName);

    /**
     * 定位到下一次匹配pattern的行
     * @param pattern 查找的字符串如20160802
     * @param fileName 文件名
     * @return
     */
    String setLogOffsetByPattern(String pattern,String fileName);

    /**
     * 对日志中的行过滤，符合条件的将被筛掉
     * @param pattern 匹配的正则表达式
     * @param fileName 要匹配的文件名
     * @return 成功true
     */
    boolean setExcludeFilter(String pattern,String fileName);

    /**
     * 对日志中的行过滤，不符合条件的将被筛掉
     * @param pattern 匹配的正则表达式
     * @param fileName 文件名
     * @return 成功true
     */
    boolean setIncludeFileter(String pattern,String fileName);

    /**
     * 从filename获取接下来的len行日志（被筛掉的日志不被len包含）
     * @param len 行数 未付则向文件开头数
     * @param filename 文件名
     * @return 日志中符合要求的行的列表
     */
    String getNext(int len,String filename);

    /**
     * 删除日志
     * @param fileName
     * @return
     */
    String delete(String[] fileName);
}
