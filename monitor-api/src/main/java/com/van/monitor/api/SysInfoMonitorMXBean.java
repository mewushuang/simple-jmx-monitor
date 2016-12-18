package com.van.monitor.api;

/**
 * Created by van on 2016/11/15.
 */
public interface SysInfoMonitorMXBean {
    String BEAN_NAME = "com.van.sysInfo.defaultName:type=resourceMonitor";

    /**
     * 获取元数据信息，一般不会变化
     * @return josn格式的字符串
     */
    String getInvariableInfo();
    /**
     * 获取状态的断面信息，随时间变化
     * @return josn格式的字符串
     */
    String getChangefulInfo();
}
