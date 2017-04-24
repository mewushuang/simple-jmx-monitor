package com.van.service;

import com.van.common.PacketRecorder;
import com.van.common.RtRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 通过控制com.van.common.SeatRecorder的logger的appender达到控制输入目标和保存策略
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
        this.recorder=new RtRecorder();
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
			logger.warn("validate failed, the raw message is:\n"+rawMsg);
			return answerprefix+getLenStr(headName.length())+headName+answerFailFlag;//+answerDelimiter;
		}else{
			String headName=getHeadName(rawMsg);
			return answerprefix+getLenStr(headName.length())+headName+answerSuccFlag;//+answerDelimiter;
		}
    }

    public static void main(String[] args) {
        String test="254109P02_0300614554{\"time\":\"2017-03-22 17:29:32\",\"data\":[{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"61403\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"42410\"},{\"dim\":\"pon\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"66103\"},{\"dim\":\"pon\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"63101\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"65411\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"42411\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"34401\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"43408\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"65401\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"21412\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"11409\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"43404\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"11413\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"32408\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"37407\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"61409\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"32406\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"50408\"},{\"dim\":\"pon\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"41101\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"66402\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"51403\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"22404\"},{\"dim\":\"pon\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"37101\"},{\"dim\":\"pon\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"21102\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"43405\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"21411\"},{\"dim\":\"pon\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"15101\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"14409\"},{\"dim\":\"pon\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"64101\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"37414\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"37406\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"35403\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"32402\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"35409\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"41403\"},{\"dim\":\"pon\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"42102\"},{\"dim\":\"pon\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"35101\"},{\"dim\":\"pon\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"32101\"},{\"dim\":\"pon\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"33101\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"22406\"},{\"dim\":\"pon\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"23101\"},{\"dim\":\"pon\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"31102\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"33404\"},{\"dim\":\"pon\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"34101\"},{\"dim\":\"pon\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"43101\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"31402\"},{\"dim\":\"pon\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"11102\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"35402\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"22403\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"36410\"},{\"dim\":\"cn\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"71101\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"62409\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"50405\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"11499\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"12402\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"36401\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"62413\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"33403\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"13403\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"64403\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"50440\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"43407\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"13406\"},{\"dim\":\"pon\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"51101\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"50438\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"36408\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"41405\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"41408\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"11416\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"42407\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"32407\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"63403\"},{\"dim\":\"pon\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"12101\"},{\"dim\":\"pon\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"65101\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"42412\"},{\"dim\":\"pon\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"22101\"},{\"dim\":\"pon\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"14101\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"65410\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"50407\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"43403\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"21401\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"11412\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"34413\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"14403\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"31416\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"23402\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"21406\"},{\"dim\":\"pon\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"62101\"},{\"dim\":\"pon\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"50101\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"23404\"},{\"dim\":\"pon\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"36101\"},{\"dim\":\"pon\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"61102\"},{\"dim\":\"pon\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"13102\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"37416\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"35408\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"61402\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"13404\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"37415\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"62404\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"12404\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"64401\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"50413\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"43401\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"36409\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"34414\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"36402\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"21408\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"43406\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"42413\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"64404\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"41404\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"37409\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"43402\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"37405\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"50433\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"36412\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"35407\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"65414\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"22402\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"11408\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"31418\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"43409\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"62403\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"33401\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"33405\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"34409\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"50430\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"23405\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"41410\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"13405\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"14404\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"41409\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"51421\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"13402\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"34408\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"51416\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"21404\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"35406\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"41402\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"65409\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"41417\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"50409\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"33410\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"43414\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"51414\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"66401\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"15423\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"37412\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"50431\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"11407\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"32412\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"41416\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"36456\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"33402\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"42403\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"65403\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"14407\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"34404\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"50402\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"41412\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"11414\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"11401\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"11406\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"37417\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"43411\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"21403\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"62401\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"11402\"},{\"dim\":\"dn\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"711010208\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"51405\"},{\"dim\":\"dn\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"711010116\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"63405\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"23403\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"21413\"},{\"dim\":\"dn\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"711010109\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"42404\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"12401\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"36411\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"51417\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"50439\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"36406\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"22409\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"62402\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"15421\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"51410\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"34405\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"61401\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"31409\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"41420\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"21402\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"61408\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"33411\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"22408\"},{\"dim\":\"pcn\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"7110102\"},{\"dim\":\"pcn\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"7110101\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"41401\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"34412\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"50445\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"32409\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"21407\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"11404\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"51412\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"12409\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"32405\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"31404\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"61407\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"66403\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"50451\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"12410\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"42408\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"22405\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"32411\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"41406\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"12405\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"31403\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"33409\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"12403\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"37401\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"32401\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"43412\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"37411\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"31401\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"21409\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"51411\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"23408\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"21416\"},{\"dim\":\"dn\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"711010212\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"51401\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"65412\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"22401\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"23401\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"42401\"},{\"dim\":\"dn\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"711010214\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"37410\"},{\"dim\":\"dn\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"711010117\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"34417\"},{\"dim\":\"dn\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"711010115\"},{\"dim\":\"dn\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"711010209\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"14402\"},{\"dim\":\"dn\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"711010210\"},{\"dim\":\"dn\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"711010108\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"11405\"},{\"dim\":\"dn\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"711010213\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"23414\"},{\"dim\":\"dn\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"711010114\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"50448\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"36407\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"61404\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"21410\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"36461\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"63402\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"41407\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"31406\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"50447\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"41415\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"34410\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"65408\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"21405\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"41411\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"32413\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"41414\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"34416\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"12408\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"62412\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"34403\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"43410\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"36455\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"42402\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"14401\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"50406\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"11415\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"66404\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"61406\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"36460\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"65404\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"61405\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"12407\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"35405\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"31415\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"66405\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"37402\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"37408\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"63406\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"51402\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"42409\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"22407\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"65413\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"32410\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"35404\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"35401\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"37413\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"32404\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"33406\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"33408\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"11403\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"42405\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"63401\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"41413\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"32403\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"12406\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"37404\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"65402\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"15422\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"64406\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"34406\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"36405\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"11410\"},{\"dim\":\"con\",\"v\":\"0\",\"tt\":\"03\",\"code\":\"13401\"}]}";
        System.out.println(
                new RtConsumer(new TransferConfiguration())
            .getResponse(test)
        );
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
