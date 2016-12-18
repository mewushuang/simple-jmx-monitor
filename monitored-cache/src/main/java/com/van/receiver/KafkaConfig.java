package com.van.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

@Component
public class KafkaConfig {
    private final Logger logger = LoggerFactory.getLogger(KafkaConfig.class);
    // Singlteon

    private Properties consumer = new Properties();
    private Properties producer = new Properties();

    private KafkaConfig() {
        try {
            //application.confDir
            String path = System.getProperty("app.home");
            logger.info("path:" + path);
            if (path == null) {
                consumer.load(new InputStreamReader(
                        KafkaConfig.class.getClassLoader().getResourceAsStream("kafka.properties")));
                producer.load(new InputStreamReader(
                        KafkaConfig.class.getClassLoader().getResourceAsStream("kafka.properties")));
            } else {
                File home=new File(path);
                File producerf=new File(home,"conf"+ File.separator+"kafka.properties");
                File consumerf=new File(home,"conf"+ File.separator+"kafka.properties");
                producer.load(new InputStreamReader(new FileInputStream(producerf), Charset.forName("utf-8")));
                consumer.load(new InputStreamReader(new FileInputStream(consumerf), Charset.forName("utf-8")));
            }
        } catch (IOException e) {
            logger.error("error loading from file kafka.properties. check if exists or is damaged", e);
        }
    }


    public Properties getConsumer() {
        return consumer;
    }

    public void setConsumer(Properties consumer) {
        this.consumer = consumer;
    }

    public Properties getProducer() {
        return producer;
    }

    public void setProducer(Properties producer) {
        this.producer = producer;
    }
}
