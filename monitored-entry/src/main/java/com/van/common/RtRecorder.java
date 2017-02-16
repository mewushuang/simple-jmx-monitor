package com.van.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by van on 17-1-11.
 */
public class RtRecorder implements PacketRecorder{
    private final Logger logger = LoggerFactory.getLogger(RtRecorder.class);



    @Override
    public void write(String msg, String ps) {

            logger.trace(msg+"\n",ps);

    }

    @Override
    public void write(String msg) {
        logger.trace(msg);
    }
}
