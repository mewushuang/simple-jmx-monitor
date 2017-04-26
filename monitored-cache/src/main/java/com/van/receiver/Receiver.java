/*
package com.van.receiver;

import com.van.service.TaskDefineService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class Receiver {

    private static Logger logger = LoggerFactory.getLogger(Receiver.class);

    @Autowired
    KafkaConfig kafkaConfig;

    @Value("${kafka.topics.rtData}")
    private String rtData;

    @Value("${kafka.topics.seatData}")
    private String seatData;

    @Autowired
    private TaskDefineService taskDefineService;

    private volatile boolean closing=false;
    //用于jmx监控
    private volatile long offset=0;
    */
/**
     * 接收消息，异常退出后只需重新启动该方法。
     *
     * 废弃！
     * 新版的api与jar包不和，改用ReceiverWithOldAPI的receiveMessage方法
     *//*

    @Deprecated
    public void receiveMessage() {
        closing=false;
        //kafka消费者不是线程安全的
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(kafkaConfig.getConsumer());
        consumer.subscribe(Arrays.asList(rtData,seatData));
        if (logger.isInfoEnabled()) {
            logger.info("waiting for msg on topic: " + rtData+","+seatData);
        }
        try {
            while (!closing) {
                ConsumerRecords<String, String> records = consumer.poll(100);
                for (ConsumerRecord<String, String> record : records) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("offset = %d, key = %s, len of value = %d", record.offset(), record.key(), record.value().length()));
                    }
                    //taskDefineService.cacheTask(new ReceiverWithOldAPI.Packet());
                    this.offset=record.offset();
                }
            }
        } finally {
            if (logger.isInfoEnabled()) {
                logger.info("consumer on topic " + rtData+","+seatData + " is closing");
            }
            consumer.close();
        }
    }

    public String getRtData() {
        return rtData;
    }

    public void setRtData(String rtData) {
        this.rtData = rtData;
    }

    public String getSeatData() {
        return seatData;
    }

    public void setSeatData(String seatData) {
        this.seatData = seatData;
    }


    public void stopGracefully(){
        this.closing=true;
    }

    public long getOffset() {
        return offset;
    }
}
*/
