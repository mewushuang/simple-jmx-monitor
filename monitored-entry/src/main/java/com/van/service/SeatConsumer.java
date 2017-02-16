package com.van.service;

import com.van.common.PacketRecorder;
import com.van.common.SeatRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by rabit on 2016/6/2.
 */
public class SeatConsumer implements PacketConsumer {
	private final static Logger logger = LoggerFactory.getLogger(SeatConsumer.class);
    private PacketRecorder recorder;

    private boolean logPackets=false;

    /*写入kafka时的配置项*/
	private final String consumePrefix ="01";//contextId
	private final String counsume2ndPrefix ="0101";//appId
	private final String consumeDelimiter ="-";


	private com.van.common.kafka.Sender sender;
    private final  String topic;
	private final String propertyPrefix="seat.";
    private TransferConfiguration config;
    private String answerDelimiter;

	public SeatConsumer(TransferConfiguration config) {
        this.config=config;
        this.topic="data.seat";
        this.answerDelimiter ="\0";
        if(config.getInt(propertyPrefix+"logRecordPacket")==1){
            this.logPackets=true;
        }
        this.recorder=new SeatRecorder();
	}

	public synchronized com.van.common.kafka.Sender getSender(){
		if(sender==null){
            sender=new com.van.common.kafka.Sender();
        }
        return sender;
	}



    @Override
    public String getInitParam() {
        return config.get(propertyPrefix+"initMsg").toString();//+ answerDelimiter;
    }


    @Override
    public String getResponse(String rawMsg) {
        return null;
    }


    
    @Override
    public boolean isValid(String rawMsg){
        return true;
    }

    @Override
    public void releaseResource() {
        getSender().close();
        logger.info("mq sender closed by seat consumer");
    }

    @Override
    public void consume(String rawMsg) {
        doCunsume(rawMsg);
    }

    private void doCunsume(String rawMsg){
    	try {
    		getSender().sendMessage(topic,"",rawMsg);
            if(logPackets) {
                recorder.write(rawMsg);
            }
        } catch (Exception e) {
            //解析错误记录日志
            logger.error("parse failure or kafka failure:"+rawMsg,e);
            if (logPackets) {
                recorder.write(rawMsg, "parse failure or kafka failure");
            }
        }
    }

    private String getHeadName(String rawMsg){
        int nameLen=Integer.parseInt(rawMsg.substring(4, 6));
        return rawMsg.substring(6, 6+nameLen);
    }





}
