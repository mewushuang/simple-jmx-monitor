package com.van;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.logging.LoggingApplicationListener;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executor;

/**
 * Created by van on 2016/12/11.
 */
@SpringBootApplication
@EnableAsync
public class SpringApp extends AsyncConfigurerSupport{

    @Bean
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("redis-persist-");
        executor.initialize();
        return executor;
    }

    /**
     * 移除springboot自带的log system
     * @param args
     * @return
     */
    public static ConfigurableApplicationContext run(String[] args){
        SpringApplication application =
                new SpringApplication(SpringApp.class);

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
}
