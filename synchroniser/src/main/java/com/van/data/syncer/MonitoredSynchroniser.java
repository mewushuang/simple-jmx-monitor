package com.van.data.syncer;

import com.van.data.syncer.schedule.TaskManager;
import com.van.monitor.api.Metric;
import com.van.monitor.api.MonitoredService;
import com.van.monitor.api.RunningStatusMetric;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by van on 2016/12/28.
 */
public class MonitoredSynchroniser implements MonitoredService {
    private final Logger logger = LoggerFactory.getLogger(MonitoredSynchroniser.class);
    private final String serviceName="dbsynchroniser";
    private final String conf="--spring.config.location=";

    private ApplicationContext applicationContext;
    private Scheduler scheduler;

    private RunningStatusMetric.RunningStatus status;
    private final Object lock = new Object();

    @Override
    public void stop(String[] strings) {
        synchronized (lock) {
            if (status != RunningStatusMetric.RunningStatus.stopping &&
                    status != RunningStatusMetric.RunningStatus.stopped) {
                status = RunningStatusMetric.RunningStatus.stopping;
            } else return;
        }
        logger.info(serviceName+" service stopping");
        //终止监听循环，在start的finally块中关闭applicationContext
        release();
        synchronized (lock) {
            status = RunningStatusMetric.RunningStatus.stopped;
        }
        logger.info(serviceName+" service stopped");
    }

    private void release() {
        if (scheduler != null)
            try {
                scheduler.shutdown();
            } catch (SchedulerException e) {
            }
        if (applicationContext != null) SpringApplication.exit(applicationContext);
    }

    @Override
    public void start(String[] strings) {
        logger.info(serviceName+" service starting");
        synchronized (lock) {
            if (status != RunningStatusMetric.RunningStatus.starting
                    && status != RunningStatusMetric.RunningStatus.running) {
                status = RunningStatusMetric.RunningStatus.starting;
            } else {
                return;
            }
        }
        //添加一个参数用以定位application.yaml
        String[] args=addSpringBootArg(strings);
        try {
            this.applicationContext = SpringApplication.run(Synchroniser.class, args);
            TaskManager taskManager = applicationContext.getBean(TaskManager.class);
            SchedulerFactory factory = new StdSchedulerFactory();
            this.scheduler = factory.getScheduler();
            synchronized (lock) {
                this.status = RunningStatusMetric.RunningStatus.running;
            }
            taskManager.start(this.scheduler);
            logger.info("scheduled task arranged");
        } catch (Exception e) {
            logger.error("program exit 1, running with exception:", e);
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
        return null;
    }

    private String[] addSpringBootArg(String[] args){
        int len=0;
        if(args!=null) len= args.length;
        String[] ret=new String[len+1];
        System.arraycopy(args,0,ret,0,args.length);
        ret[len]= conf+ Paths.get(System.getProperty("user.dir"),"conf","application.yaml").toAbsolutePath().toString();
        return ret;
    }
}
