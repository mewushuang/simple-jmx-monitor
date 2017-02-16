package com.van.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.van.receiver.KafkaConfig;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * he producer is thread safe and sharing a single producer instance across threads
 * will generally be faster than having multiple instances.
 * Created by van on 2016/12/10.
 */
@Service
public class ProducerService {
    private final Logger logger= LoggerFactory.getLogger(ProducerService.class);
    private ObjectMapper mapper=new ObjectMapper();

    @Autowired
    private KafkaConfig kafkaConfig;

    private Producer<String, String> producer=null;

    @PostConstruct
    public void init(){
        producer= new Producer<>(new ProducerConfig(kafkaConfig.getProducer()));
        logger.info("producer initialized,metadata.broker.list is:"+kafkaConfig.getProducer().getProperty("metadata.broker.list"));
    }


    public void send(String topic,String key,String value){
        if (logger.isDebugEnabled()) {
            //logger.debug("send msg to MQ with key: "+key+" value:\n"+value);

        }
        producer.send(new KeyedMessage<>(topic, key, value));
    }

    public void send(String topic, List<String> values){
        for (String val:values){
            send(topic,null,val);
        }
    }
    public void noticeChangedKeys(String topic,List<String> keys) {
        //68H11{"keyid": ["1-2-307-P01_02001-1"," 1-2-307-P01_02002-2"," 1-2-307-P01_02003-1"]}\n
        Map<String,List<String>> queueMsg= new HashMap<>(1);

        queueMsg.put("keyid", keys);
        String json;
        try {
            json = mapper.writeValueAsString(queueMsg);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        send(topic,null, "68H11"+json+"\n");


    }

    @PreDestroy
    public void release(){
        if(producer!=null){
            producer.close();
            logger.info("producer close");
        }
    }

}
