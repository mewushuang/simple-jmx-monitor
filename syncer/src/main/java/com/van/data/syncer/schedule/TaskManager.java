package com.van.data.syncer.schedule;

import com.van.data.syncer.config.ScheduledTaskConfiguration;
import com.van.data.syncer.deleteAndInsertSyncer.SyncService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by van on 2016/12/27.
 */
@Service
public class TaskManager {

    @Autowired
    private ScheduledTaskConfiguration taskConfiguration;
    @Autowired
    private SyncService syncService;

    private Scheduler scheduler;


    public void start(Scheduler scheduler) throws SchedulerException {
        this.scheduler=scheduler;
        scheduler.start();
        for(ScheduledTaskConfiguration.ScheduledTask task:taskConfiguration.getTasks()){
            JobDataMap context=new JobDataMap();
            context.put("syncService",syncService);
            context.put("sql",task.getSql());
            context.put("des",task.getDes());
            JobDetail job =JobBuilder.newJob(SyncJob.class)
                    .usingJobData(context)
                    .withIdentity(task.getName(),"sync")
                    .build();
            Trigger trigger= TriggerBuilder.newTrigger()
                    .withIdentity(task.getCron(),task.getName())
                    .startNow()
                    .withSchedule(
                            CronScheduleBuilder.cronSchedule(task.getCron()))
                    .build();
        scheduler.scheduleJob(job,trigger);
        }
        scheduler.start();
    }


}

