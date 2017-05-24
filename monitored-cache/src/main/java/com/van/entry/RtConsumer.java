package com.van.entry;

import com.van.receiver.EntryConfig;
import com.van.service.TaskDefineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Component;

/**
 * 通过控制com.van.common.SeatRecorder的logger的appender达到控制输入目标和保存策略
 * Created by rabit on 2016/6/2.
 */
@Component
public class RtConsumer implements PacketConsumer {
    private final static Logger logger = LoggerFactory.getLogger(RtConsumer.class);
    private PacketRecorder recorder;

	/*写响应的配置项*/

    private static String answerDelimiter;
    private static String answerprefix;
    private static String answerSuccFlag;
    private static String answerFailFlag;


    @Autowired
    private EntryConfig config;
    @Autowired
    private TaskDefineService taskDefineService;

    public RtConsumer() {
        this.answerDelimiter = "\0";
        this.answerprefix = "254";
        this.answerSuccFlag = "1";
        this.answerFailFlag = "0";
        this.recorder = new RtRecorder();
    }

    @Override
    public String getInitParam() {
        return config.getRtInitMsg();// + answerDelimiter;
    }


    /**
     * 使用mina后
     * TextLineCodecFactory codecFactory
     * = new TextLineCodecFactory(Charset.forName("utf-8"), "\0", "\0");
     * 该codecFactory会为每一个输入结尾添加'\0', 此处不必手动添加
     *
     * @param rawMsg
     * @return
     */
    @Override
    public String getResponse(String rawMsg) {
        if (!isValid(rawMsg)) {
            String headName = getHeadName(rawMsg);
            logger.warn("validate failed, the raw message is:\n" + rawMsg);
            return answerprefix + getLenStr(headName.length()) + headName + answerFailFlag;//+answerDelimiter;
        } else {
            String headName = getHeadName(rawMsg);
            return answerprefix + getLenStr(headName.length()) + headName + answerSuccFlag;//+answerDelimiter;
        }
    }


    private static String getLenStr(int i) {
        if (i < 10) {
            return "0" + i;
        } else
            return "" + i;

    }

    @Override
    public boolean isValid(String msg) {
        if (msg == null || msg.isEmpty()) {
            return false;
        }

        try {
            if (!msg.startsWith("2541")) {
                return false;
            }

            String head = getHeadName(msg);
            int offset = msg.indexOf("{");
            int msgLen = Integer.parseInt(msg.substring(6 + head.length(), offset));
            int realLen = msg.length() - offset;
            return msgLen == realLen;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void releaseResource() {

    }

    @Override
    public void consume(String rawMsg) {
        doCunsume(rawMsg);
    }

    private void doCunsume(String rawMsg) {
        try {
            //Thread.sleep(1000*10000);
            String headName = getHeadName(rawMsg);
            String msg = rawMsg.substring(rawMsg.indexOf("{"));
            Packet packet = new Packet(Client.RT_MODULE, headName, msg);
            //sender.sendMessage(topic, headName, msg);

            taskDefineService.doAsync(packet);


            //写入日志文件
            if (config.isRtIfLog()) {
                recorder.write(rawMsg);
            }
        } catch (TaskRejectedException e) {
            logger.error("async task queue is full !", e);
        } catch (Exception e) {
            //解析错误记录日志
            if (logger.isErrorEnabled()) {
                logger.error("parse failure or kafka failure:" + rawMsg, e);
            }
            if (config.isRtIfLog()) {
                recorder.write(rawMsg, "parse failure or kafka failure");
            }
        }
    }


    private String getHeadName(String rawMsg) {
        int nameLen = Integer.parseInt(rawMsg.substring(4, 6));
        return rawMsg.substring(6, 6 + nameLen);
    }


}
