package com.van.service;

import com.van.common.PacketRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by rabit on 2016/6/2.
 */
public class RtConsumer implements PacketConsumer {
	private final static Logger logger = LoggerFactory.getLogger(RtConsumer.class);
    private PacketRecorder recorder;

    private boolean logPackets=false;

    /*写入kafka时的配置项*/
	private final String consumePrefix ="01";//contextId
	private final String counsume2ndPrefix ="0101";//appId
	private final String consumeDelimiter ="-";
    private final String propertyPrefix="rt.";

	/*写响应的配置项*/

	private static String answerDelimiter;
	private static String answerprefix;
	private static String answerSuccFlag;
	private static String answerFailFlag;


	private com.van.common.kafka.Sender sender;
    private final String topic;
    private TransferConfiguration config;
	public RtConsumer(TransferConfiguration config) {
		sender=new com.van.common.kafka.Sender();
        this.config=config;
        this.topic="data.rt";
        this.answerDelimiter="\0";
        this.answerprefix="254";
        this.answerSuccFlag="1";
        this.answerFailFlag="0";

        if(config.getInt(propertyPrefix+"logRecordPacket")==1){
            this.logPackets=true;
        }
        this.recorder=PacketRecorder.instance(config.getString(propertyPrefix+"logNamePrefix"));
	}

    @Override
    public String getInitParam() {
        return config.get(propertyPrefix+"initMsg").toString()+answerDelimiter;
    }


    /**
     * 使用mina后
     * TextLineCodecFactory codecFactory
     * = new TextLineCodecFactory(Charset.forName("utf-8"), "\0", "\0");
     * 该codecFactory会为每一个输入结尾添加'\0', 此处不必手动添加
     * @param rawMsg
     * @return
     */
    @Override
    public String getResponse(String rawMsg) {
		if(!isValid(rawMsg)){
			String headName=getHeadName(rawMsg);
			return answerprefix+getLenStr(headName.length())+headName+answerFailFlag;//+answerDelimiter;
		}else{
			String headName=getHeadName(rawMsg);
			return answerprefix+getLenStr(headName.length())+headName+answerSuccFlag;//+answerDelimiter;
		}
    }

	private static String getLenStr(int i){
		if(i<10){
			return "0"+i;
		}else
			return ""+i;

	}
    
    @Override
    public boolean isValid(String msg){
		if (msg == null || msg.isEmpty()) {
			return false;
		}

		try {
			if(!msg.startsWith("2541")){
				return false;
			}

			String head=getHeadName(msg);
			int offset=msg.indexOf("{");
			int msgLen=Integer.parseInt(msg.substring(6+head.length(),offset));
			int realLen=msg.length()-offset;
			return msgLen==realLen;
		} catch (Exception e) {
			return false;
		}
    }

    @Override
    public void releaseResource() {
        sender.close();
        logger.info("mq sender closed by rt consumer");
    }

    @Override
    public void consume(String rawMsg) {
        doCunsume(rawMsg);
    }

    private void doCunsume(String rawMsg){
        try {
            //Thread.sleep(1000*10000);
            String headName=getHeadName(rawMsg);
            String msg=rawMsg.substring( rawMsg.indexOf("{"));
            sender.sendMessage(topic,headName,msg);
            //写入日志文件
            if(logPackets) {
                recorder.write(rawMsg);
            }
        } catch (Exception e) {
            //解析错误记录日志
            if (logger.isErrorEnabled()) {
                logger.error("parse failure or kafka failure:"+rawMsg,e);
            }
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
