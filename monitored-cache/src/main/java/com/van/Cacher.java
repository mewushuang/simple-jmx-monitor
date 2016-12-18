package com.van;

import com.van.monitor.api.Metric;
import com.van.monitor.api.MonitoredService;
import com.van.monitor.api.RunningStatusMetric;
import com.van.receiver.ReceiverWithOldAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by van on 2016/11/29.
 */
public class Cacher implements MonitoredService {

    private final Logger logger = LoggerFactory.getLogger(Cacher.class);

    private ApplicationContext applicationContext;
    private ReceiverWithOldAPI receiver;

    private RunningStatusMetric.RunningStatus status;
    private final Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        //String propertyFile = System.getProperty("user.dir") + File.separator + "conf" + File.separator + "rtdbsource.properties";
        //System.out.println(propertyFile);
        ReceiverWithOldAPI receiver = SpringApplication.run(SpringApp.class, args).getBean(ReceiverWithOldAPI.class);
        receiver.receiveMessage();
    }


    @Override
    public void stop(String[] strings) {
        synchronized (lock) {
            if (status != RunningStatusMetric.RunningStatus.stopping &&
                    status != RunningStatusMetric.RunningStatus.stopped) {
                status = RunningStatusMetric.RunningStatus.stopping;
            } else return;
        }
        logger.info("persistence service stopping");
        //终止监听循环，在start的finally块中关闭applicationContext
        if (receiver != null) receiver.stopGracefully();
        release();
        synchronized (lock) {
            status = RunningStatusMetric.RunningStatus.stopped;
        }
        logger.info("persistence service stopped");
    }

    private void release() {
        if (applicationContext != null) SpringApplication.exit(applicationContext);
    }

    @Override
    public void start(String[] strings) {
        logger.info("persistence service starting");
        synchronized (lock) {
            if (status != RunningStatusMetric.RunningStatus.starting
                    && status != RunningStatusMetric.RunningStatus.running) {
                status = RunningStatusMetric.RunningStatus.starting;
            } else {
                return;
            }
        }

        try {
            System.setProperty("spring.config.location",System.getProperty("app.home")+"/conf");
            this.applicationContext = SpringApplication.run(SpringApp.class, strings);
            this.receiver = applicationContext.getBean(ReceiverWithOldAPI.class);
            synchronized (lock) {
                this.status = RunningStatusMetric.RunningStatus.running;
            }
            receiver.receiveMessage();
        } catch (Exception e) {
            logger.error("running with exception:", e);
        }
    }

    @Override
    public void startDefault(Executor pool) {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                start(new String[]{});
            }
        });
    }

    @Override
    public void stopDefault() {
        stop(new String[]{});
    }

    @Override
    public RunningStatusMetric getRunningStatus(String[] strings) {
        return new RunningStatusMetric(status);
    }

    @Override
    public List<Metric> getExtraMetrics(String[] strings) {
        List<Metric> list = new ArrayList<>(5);

        long offset = 0L;
        if (receiver != null) {
            offset=receiver.getOffset();
        }
        list.add(new OffsetMetric(offset));
        return list;
    }

    static class OffsetMetric extends Metric {
        //Metric(String name, String value, boolean needSave, String description)
        public OffsetMetric(long offset) {
            super("consumer offset", "" + offset, false, "kafaka消费者队列的offset");
        }

        @Override
        public Level getLevel() {
            return Level.info;
        }
    }
}
