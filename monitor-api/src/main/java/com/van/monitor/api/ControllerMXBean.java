package com.van.monitor.api;

/**
 * Created by van on 2016/11/15.
 */
public interface ControllerMXBean {
    String BEAN_TYPE = ":type=serviceController";


    /**
     * 启动前根据需要预先判断，防止实例被多次启动
     * @return
     */
    String start(String[] args);
    /**
     * 默认启动逻辑：在监控后台启动时被调用一次
     */
    String startDefault();

    String stopDefault();

    String stop(String[] args);
    String status(String[] args);

    /**
     * 关闭服务和jmx守护线程
     * @return
     */
    void shutDownDaemon();

    /**
     * 重启进程
     * @return
     */
    String restartDaemon();
}
