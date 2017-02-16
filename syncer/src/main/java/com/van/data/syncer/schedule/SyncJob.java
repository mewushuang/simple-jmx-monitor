package com.van.data.syncer.schedule;

import com.van.data.syncer.deleteAndInsertSyncer.SyncService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by van on 2016/12/27.
 */
public class SyncJob implements Job {
    private final Logger logger= LoggerFactory.getLogger(SyncJob.class);
    private final int batchSize=1000;
    public SyncJob(){
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobKey key = context.getJobDetail().getKey();
        logger.info("job "+key+" triggered");
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();

        String sql = dataMap.getString("sql");
        String des = dataMap.getString("des");
        SyncService syncService= (SyncService) dataMap.get("syncService");
        syncService.sync(sql,des);

        logger.info("job "+key+" is done for current trigger");

    }
}
