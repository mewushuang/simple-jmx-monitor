package com.van.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 通过控制com.van.common.SeatRecorder的logger的appender达到控制输入目标和保存策略
 * Created by van on 17-1-11.
 */
public class SeatRecorder implements PacketRecorder {
    private final Logger logger = LoggerFactory.getLogger(SeatRecorder.class);



    @Override
    public void write(String msg, String ps) {

        logger.trace(msg+"\n",ps);

    }

    @Override
    public void write(String msg) {
        logger.trace(msg);
    }
}
