package com.van.monitor.example;

import com.van.monitor.api.Metric;
import com.van.monitor.api.MonitoredService;
import com.van.monitor.api.RunningStatusMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by van on 2016/11/16.
 */
public class DefaultService implements MonitoredService {
    private Logger logger= LoggerFactory.getLogger(DefaultService.class);
    public volatile boolean isStopping=true;


    @Override
    public void stop(String[] key){
        String k=key==null?"\"\"": Arrays.asList(key).toString();
        logger.info("service stopping:"+this.getClass().getName()+", the key is "+k);
        this.isStopping=true;
    }
    @Override
    public void start(String[] key){
        String k=key==null?"\"\"": Arrays.asList(key).toString();
        logger.info("service starting:"+this.getClass().getName()+", the key is "+k);
        isStopping=false;
        while (!isStopping&&!Thread.currentThread().isInterrupted()){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
        logger.info("service stopped:"+this.getClass().getName()+", the key is "+k);
    }

    /**
     * 默认启动逻辑：在监控后台启动时被调用一次
     */
    @Override
    public void startDefault(Executor pool) {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                start(null);
            }
        });
    }

    /**
     * 默认关闭逻辑：在监控后台关闭时被调用一次
     */
    @Override
    public void stopDefault() {
        stop(null);
    }

    /**
     * 获取服务运行状态的Metric对象，每个服务里面必须有，如果返回空会导致异常
     *
     * @param key
     * @return
     */
    @Override
    public RunningStatusMetric getRunningStatus(String[] key) {
        String k=key==null?"\"\"": Arrays.asList(key).toString();
        logger.debug("getting running status:"+this.getClass().getName()+", the key is "+k);
        return new RunningStatusMetric(isStopping ?
                RunningStatusMetric.RunningStatus.stopped
                : RunningStatusMetric.RunningStatus.running);
    }

    /**
     * 获取其它自定义的Metric对象,没有返回null
     *
     * @param key
     * @return
     */
    @Override
    public List<Metric> getExtraMetrics(String[] key) {
        String k=key==null?"\"\"": Arrays.asList(key).toString();
        logger.debug("getting extra metrics:"+this.getClass().getName()+", the key is "+k);
        return null;
    }


}
