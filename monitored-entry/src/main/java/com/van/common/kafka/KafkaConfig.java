package com.van.common.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;


public class KafkaConfig {
	private static Logger logger= LoggerFactory.getLogger(KafkaConfig.class);

	// Singlteon
	private static final String consumer_Pro_File = "kafka.properties";
	private static final String producer_Pro_File = "kafka.properties";
	private static Properties consumer;
	private static Properties producer;
	private static KafkaConfig kafkaConfig = new KafkaConfig();

	private KafkaConfig() {
	}

	public static KafkaConfig getInstance() {
		return kafkaConfig;
	}

	static {
		String path=null;
		try {
			//application.confDir
			path = System.getProperty("app.home");
			consumer = new Properties();
			producer = new Properties();
			if (path == null) {
				consumer.load(new InputStreamReader(
						KafkaConfig.class.getClassLoader().getResourceAsStream(consumer_Pro_File)));
				producer.load(new InputStreamReader(
						KafkaConfig.class.getClassLoader().getResourceAsStream(producer_Pro_File)));
			} else {
				File home=new File(path);
				File producerf=new File(home,"conf"+File.separator+producer_Pro_File);
				File consumerf=new File(home,"conf"+File.separator+consumer_Pro_File);
				producer.load(new InputStreamReader(new FileInputStream(producerf), Charset.forName("utf-8")));
				consumer.load(new InputStreamReader(new FileInputStream(consumerf), Charset.forName("utf-8")));
			}
		} catch (IOException e) {
			logger.error("error loading from file "+(path==null?"classpath":"conf/"+producer_Pro_File+","+consumer_Pro_File)+". check if exists or is damaged", e);
		}
	}

	public static Properties getConsumer() {
		return consumer;
	}

	public static Properties getProducer() {
		return producer;
	}
}
