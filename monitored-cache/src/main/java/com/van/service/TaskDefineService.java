package com.van.service;

import com.van.common.ScodeEntity;
import com.van.receiver.ReceiverWithOldAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by van on 2016/12/10.
 */
@Service
public class TaskDefineService {
    @Autowired
    private ParseService parseService;

    @Autowired
    private RTDBService rtdbService;

    @Autowired
    private ReceiverWithOldAPI receiverWithOldAPI;

    private final Logger logger= LoggerFactory.getLogger(TaskDefineService.class);


    @Async
    public void cacheTask(ReceiverWithOldAPI.Record record){
        try {
            if(record.topic().equals(receiverWithOldAPI.getRtData())){
                String scode=record.key();
                ScodeEntity entity=parseService.parseRawMsgOfRt(scode,record.value());
                rtdbService.saveEntity(entity);
            }else {
                List<ScodeEntity> entitys=parseService.parseRawMsgOfSeat(record.value());
                for(ScodeEntity s:entitys){
                    rtdbService.saveEntity(s);
                }
            }
        } catch (Exception e) {
            logger.error("error dealing with record,topic["+record.topic()+"],offset["+record.topic()+"]"+e.getMessage()+"\n\t value is:"+record.value(),e);

        }
    }
}
