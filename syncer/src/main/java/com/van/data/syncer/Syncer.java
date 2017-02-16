package com.van.data.syncer;

import com.van.data.syncer.schedule.TaskManager;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by van on 2016/12/27.
 */
@SpringBootApplication
@EnableScheduling
public class Syncer {

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext applicationContext=SpringApplication.run(Syncer.class,args);
        TaskManager c=applicationContext.getBean(TaskManager.class);
        SchedulerFactory factory=new StdSchedulerFactory();
        Scheduler scheduler=null;
        try {
            scheduler=factory.getScheduler();
            c.start(scheduler);
            System.out.println("started");
            Thread.sleep(1000*10000L);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }finally {
            try {
                scheduler.shutdown();
            } catch (SchedulerException e) {
            }
        }
    }
}
