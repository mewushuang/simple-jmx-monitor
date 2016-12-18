package com.van.monitor.example;

import com.van.monitor.api.ControllerMXBean;
import com.van.monitor.api.Metric;
import com.van.monitor.api.MonitoredService;
import com.van.monitor.util.ResponseJsonSerialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.remote.JMXConnectorServer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by van on 2016/11/16.
 */
public class ServerMB extends ResponseJsonSerialization implements ControllerMXBean {

    private Logger logger = LoggerFactory.getLogger(ServerMB.class);

    private MonitoredService service;
    private Executor threadPool;
    private JMXConnectorServer serverForShutDown;

    /**
     * 关闭jmx主线程的flag，放在这里以便能从jmx客户端控制
     * 只在本类中适用，并未抽象到ControllerMXBean（用于使用命令行关闭整个程序）
     */
    private volatile boolean shutDownDaemon=false;

    /**
     *
     * @param threadPool
     * @param serviceClassName 配置读取服务类名，通过反射的方式加载
     */
    public ServerMB(Executor threadPool, String serviceClassName, JMXConnectorServer serverForShutdown) {
        this.threadPool = threadPool;
        this.serverForShutDown=serverForShutdown;
        try {
            this.service = (MonitoredService) Class.forName(serviceClassName).newInstance();
            logger.info("service injected:"+serviceClassName);
        } catch (ClassNotFoundException e) {
            logger.error("service class not found:"+serviceClassName);
        } catch (InstantiationException e) {
            logger.error("service class cannot be instantiates:"+serviceClassName);
        } catch (IllegalAccessException e) {
            logger.error("no permission on instantiation method of service class:"+serviceClassName);
        }
    }


    /**
     * 启动前根据需要预先判断，防止实例被多次启动
     * @return
     */
    @Override
    public String start(final String[] args) {
        // 预先判断，如已启动则直接返回。该逻辑现转移到服务内部处理
        /*if (!service.isStopping) {
            return getResponse(400, "service had been started", null);
        }*/
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                service.start(args);
            }
        });
        return getEmptySuccResponse();
    }

    /**
     * 默认启动逻辑：在监控后台启动时被调用一次
     */
    @Override
    public String startDefault() {
        service.startDefault(threadPool);
        return getEmptySuccResponse();
    }

    @Override
    public String stop(String[] args) {
        // 预先判断，如已启动则直接返回。该逻辑现转移到服务内部处理
        /*if (service.isStopping) {
            return getResponse(400, "service had been stopped", null);
        }*/
        service.stop(args);
        return getEmptySuccResponse();
    }

    @Override
    public String restart(String[] args) {
        return getResponse(400, "service had been stopped", null);
    }

    /**
     * 可以在ret添加新的参数。但不可更改返回的结构：即List<Metric>的json格式，且list中至少包含运行状态这一个Metric
     *
     * @return
     */
    @Override
    public String status(String[] args) {
        List<Metric> ret =service.getExtraMetrics(args);
        if(ret==null) ret=new ArrayList<>();
        ret.add(service.getRunningStatus(args));
        return serialize(ret);
    }


    public boolean isShutDownDaemon() {
        return shutDownDaemon;
    }

    public void shutDownDaemon() {
        try {
            serverForShutDown.stop();
            logger.info("connector server shutdown");
        } catch (IOException e) {
            logger.error("connector server shutdown exception:",e);
        }
        service.stopDefault();
        logger.info("user service shutdown");
        this.shutDownDaemon = true;
    }
}
