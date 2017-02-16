package com.van.common.kafka;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

public class Sender {
    private Logger logger= LoggerFactory.getLogger(Sender.class);
	Producer<String, String> producer;
	public AtomicLong trace=new AtomicLong(0);


    public Sender() {
        producer = new Producer<>(new ProducerConfig(getProducerConfig()));
    }

    /**
	 * 发送单个消息
	 * 
	 * @param topic
	 * @param msg
	 */
	public void sendMessage(String topic,String key, String msg) {

		long current=trace.addAndGet(1);
		long time=System.currentTimeMillis();
		key=current+"#"+time+"#"+key;
		logger.info(key);
		// 获取生产者
		// 生成Kafka消息格式
		KeyedMessage<String, String> message = new KeyedMessage<>(topic, key , msg);
		// 发送消息
		producer.send(message);
	}

	public void close(){
		if(producer!=null) producer.close();
	}
	private Properties getProducerConfig() {
		return KafkaConfig.getProducer();
	}
}
