package com.van.monitor.api;

import java.io.Serializable;

/**
 * Created by van on 2016/12/2.
 */
public class RunningStatusMetric extends Metric implements Serializable {

    public enum RunningStatus{
        /**
         * jmx client停止
         */
        daemonStopped,
        stopped,
        stopping,
        running,
        starting,
        blocking,
        /**
         * 无效的监控对象
         */
        delete;
    }

    /**
     * 定义了该子类的name,desc,needSave等字段，具体含义参考Metric类
     * @param status
     */
    public RunningStatusMetric(RunningStatus status) {
        super("runningStatus",status.name(),false,"服务运行状态");
        if(status==null){
            throw new IllegalArgumentException("error initializing RunningStatusMetric,status cannot be null");
        }
    }

    /**
     * 各服务根据需要重写此方法，可以计算出每个测点实例的消息级别
     * 用于在监控中心实现告警逻辑
     *
     * @return
     */
    @Override
    public Metric.Level getLevel() {
        if(RunningStatus.running.name().equals(value)||RunningStatus.starting.name().equals(value)||RunningStatus.blocking.name().equals(value)){
            return Level.info;
        }
        return Level.error;
    }


}
