package com.van.data.syncer;

import com.van.data.syncer.schedule.TaskManager;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.logging.LoggingApplicationListener;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by van on 2016/12/27.
 */
@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
public class Synchroniser {

    //for test
    public static void main(String[] args) throws InterruptedException {
        ApplicationContext applicationContext=
                SpringApplication.run(Synchroniser.class,addSpringBootArg(args));
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
        }

    }




    /**
     * 移除springboot自带的log system
     * @param args
     * @return
     */
    public static ConfigurableApplicationContext run(String[] args){
        SpringApplication application =
                new SpringApplication(Synchroniser.class);

        Collection<ApplicationListener<?>> listeners =
                new ArrayList<ApplicationListener<?>>();
        for (ApplicationListener<?> listener: application.getListeners()) {
            if (!(listener instanceof LoggingApplicationListener)) {
                listeners.add(listener);
            }
        }
        application.setListeners(listeners);

        return application.run(args);
    }









    private static String[] addSpringBootArg(String[] args){
        int len=0;
        if(args!=null) len= args.length;
        String[] ret=new String[len+1];
        System.arraycopy(args,0,ret,0,args.length);
        ret[len]= "--spring.config.location="+ Paths.get(System.getProperty("user.dir"),"conf", "application111.yaml").toAbsolutePath().toString();
        System.out.println(ret[len]);
        return ret;
    }
}
