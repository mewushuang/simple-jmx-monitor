/*
package com.van;

import com.van.common.StatusAware;
import com.van.monitor.api.Metric;
import com.van.monitor.api.MonitoredService;
import com.van.monitor.api.RunningStatusMetric;
import com.van.receiver.PacketConsumer;
import com.van.receiver.TopicConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

*
 * Created by van on 2016/11/29.


public class Cacher implements MonitoredService,StatusAware {


    public static final String SEAT_ARG = "seat";
    public static final String RT_ARG = "rt";

    private final Logger logger = LoggerFactory.getLogger(Cacher.class);

    private ApplicationContext applicationContext;
    private final String conf="--spring.config.location=";
    private PacketConsumer receiver;

    private RunningStatusMetric.RunningStatus status;
    private final Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {

        //ReceiverWithOldAPI receiver = SpringApplication.run(SpringApp.class, args).getBean(ReceiverWithOldAPI.class);
        //receiver.receiveMessage(null);

        //测试
        TopicConfig topicConfig=SpringApplication.run(SpringApp.class, args).getBean(TopicConfig.class);
        System.err.println(topicConfig.getRtData());
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
            strings=addSpringBootArg(strings);
            //正式环境有日志系统，此处移除了springboot自带的日志系统
            this.applicationContext = SpringApp.run(strings);
            this.receiver = applicationContext.getBean(PacketConsumer.class);
            synchronized (lock) {
                this.status = RunningStatusMetric.RunningStatus.running;
            }
            receiver.receiveMessage(this);
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

    @Override
    public void setStatus(RunningStatusMetric.RunningStatus status) {
        synchronized (lock){
            this.status=status;
        }
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

    private String[] addSpringBootArg(String[] args){
        int len=0;
        if(args!=null) len= args.length;
        String[] ret=new String[len+1];
        System.arraycopy(args,0,ret,0,args.length);
        ret[len]= conf+ Paths.get(System.getProperty("user.dir"),"conf").toAbsolutePath().toString();
        return ret;
    }
}
*/
