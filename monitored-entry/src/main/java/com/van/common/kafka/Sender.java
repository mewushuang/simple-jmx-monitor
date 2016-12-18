package com.van.common.kafka;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.util.Properties;

public class Sender {
	Producer<String, String> producer;


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
		// 获取生产者
		// 生成Kafka消息格式
		KeyedMessage<String, String> message = new KeyedMessage<>(topic, key, msg);
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
