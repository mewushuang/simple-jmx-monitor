package com.van.common.kafka;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observer;

public class Receiver {
	public void receiveMessage(String topic, Observer observer) {
		// 获取客户端
		ConsumerConnector connector = Consumer.createJavaConsumerConnector(createConsumerConfig());
		// 指定客户端读取主题
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, 1);
		// 获取该主题下的消息集合
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = connector.createMessageStreams(topicCountMap);
		KafkaStream<byte[], byte[]> kafkaStream = consumerMap.get(topic).get(0);
		ConsumerIterator<byte[], byte[]> it = kafkaStream.iterator();
		// 转换结果集
		while (it.hasNext()) {
			String message = new String(it.next().message());
			// 打印获取到的消息
			if (observer != null) {
				observer.update(null, message);
			}
		}
	}

	private ConsumerConfig createConsumerConfig() {
		return new ConsumerConfig(KafkaConfig.getConsumer());
	}
}
