package com.van.monitor.systemInfo;

/**
 * Created by van on 2016/11/17.
 */
public interface SeekableFile {

    boolean reset();

    /**
     * 按照大体位置进行查找
     * @param percent 0到1的小数 当前offset到结尾视作1
     * @return
     */
    boolean seekByPercent(double percent);

    /**
     * seek到下一次出现pattern的位置
     * @param pattern
     * @return
     */
    boolean seekByPattern(String pattern);

    /**
     * 从offset读取下一行内容
     * @return
     */
    LogLine nextLine();

    void close();

    // TODO: 2016/11/17 添加其他详细功能
}
