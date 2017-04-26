package com.van.service;

import com.van.common.ScodeEntity;
import com.van.entry.Client;
import com.van.entry.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by van on 2016/12/10.
 */
@Service(value = "taskDefineService")
public class TaskDefineService {
    @Autowired
    private ParseService parseService;

    @Autowired
    private RTDBService rtdbService;


    @Autowired
    private ScodeTimeRecorderService scodeTimeRecorderService;

    private final Logger logger= LoggerFactory.getLogger(TaskDefineService.class);


    @Async
    public void doAsync(Object target){
        Packet packet = (Packet) target;
        try {
            if(Client.RT_MODULE.equals(packet.module())){
                String scode= packet.key();
                ScodeEntity entity=parseService.parseRawMsgOfRt(scode, packet.value());
                rtdbService.saveEntity(entity);
                scodeTimeRecorderService.recordTime(entity);
            }else if(Client.SEAT_MODULE.equals(packet.module())) {
                List<ScodeEntity> entitys=parseService.parseRawMsgOfSeat(packet.value());
                for(ScodeEntity s:entitys){
                    rtdbService.saveEntity(s);
                    scodeTimeRecorderService.recordTime(s);
                }
            }else{
                logger.error("illegal packet,module["+ packet.module()+"],key["+packet.key()+"] content:\n"+packet.value()+"");
            }
        } catch (Exception e) {
            logger.error("error dealing with packet,module["+ packet.module()+"]"+e.getMessage()+"",e);

        }
    }
}
