package com.van.data.syncer.schedule;

import com.van.data.syncer.config.ScheduledTaskConfiguration;
import com.van.data.syncer.deleteAndInsertSyncer.SyncService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by van on 2016/12/27.
 */
@Service
public class TaskManager {
    private final Logger logger = LoggerFactory.getLogger(TaskManager.class);

    @Autowired
    private ScheduledTaskConfiguration taskConfiguration;
    @Autowired
    private SyncService syncService;

    private Scheduler scheduler;


    public void start(Scheduler scheduler) throws SchedulerException {
        this.scheduler = scheduler;
        scheduler.start();
        for (ScheduledTaskConfiguration.ScheduledTask task : taskConfiguration.getTasks()) {
            addJob(task);
        }
        scheduler.start();
    }

    public void addJob(ScheduledTaskConfiguration.ScheduledTask task) throws SchedulerException {
        JobDataMap context = new JobDataMap();
        context.put("syncService", syncService);
        context.put("sql", task.getSql());
        context.put("des", task.getDes());
        if (logger.isInfoEnabled()) {
            logger.info("new sync task added:" + System.lineSeparator() +
                    "\tname:" + task.getName() + System.lineSeparator() +
                    "\tcron:" + task.getCron() + System.lineSeparator() +
                    "\tdestination table:" + task.getDes() +System.lineSeparator()+
                    "\tsql:" + task.getSql() + System.lineSeparator());
        }
        JobDetail job = JobBuilder.newJob(SyncJob.class)
                .usingJobData(context)
                .withIdentity(task.getName(), "sync")
                .build();
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(task.getCron(), task.getName())
                .startNow()
                .withSchedule(
                        CronScheduleBuilder.cronSchedule(task.getCron()))
                .build();
        scheduler.scheduleJob(job, trigger);
    }


}

