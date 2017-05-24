package com.van.entry;

import com.van.receiver.EntryConfig;
import com.van.service.TaskDefineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Component;

/**
 * Created by rabit on 2016/6/2.
 */
@Component
public class SeatConsumer implements PacketConsumer {
    private final static Logger logger = LoggerFactory.getLogger(SeatConsumer.class);
    private PacketRecorder recorder;


    @Autowired
    private EntryConfig config;
    @Autowired
    private TaskDefineService taskDefineService;

    public SeatConsumer() {
        this.recorder = new SeatRecorder();
    }


    @Override
    public String getInitParam() {
        return config.getSeatInitMsg();//+ answerDelimiter;
    }


    @Override
    public String getResponse(String rawMsg) {
        return null;
    }


    @Override
    public boolean isValid(String rawMsg) {
        return true;
    }

    @Override
    public void releaseResource() {

    }

    @Override
    public void consume(String rawMsg) {
        doCunsume(rawMsg);
    }

    private void doCunsume(String rawMsg) {
        //getSender().sendMessage(topic,"",rawMsg);
        Packet p = new Packet(Client.SEAT_MODULE, "", rawMsg);
        try {
            taskDefineService.doAsync(p);
        } catch (TaskRejectedException e) {
            logger.error("async task queue is full !", e);
        }
        if (config.isSeatIfLog()) {
            recorder.write(rawMsg);
        }
    }

    private String getHeadName(String rawMsg) {
        int nameLen = Integer.parseInt(rawMsg.substring(4, 6));
        return rawMsg.substring(6, 6 + nameLen);
    }


}
