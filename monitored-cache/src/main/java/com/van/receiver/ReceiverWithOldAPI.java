package com.van.receiver;

import com.van.common.StatusAware;
import com.van.monitor.api.RunningStatusMetric;
import com.van.service.TaskDefineService;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Component
public class ReceiverWithOldAPI {

    private static Logger logger = LoggerFactory.getLogger(ReceiverWithOldAPI.class);

    @Autowired
    KafkaConfig kafkaConfig;
    @Value("data.rt")
    private String rtData;
    @Value("data.seat")
    private String seatData;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    private ExecutorService executor;
    @Autowired
    private TaskDefineService taskDefineService;

    private volatile boolean closing = false;
    //用于jmx监控
    private volatile long offset = 0;
    private ConsumerConnector connector;

    public ReceiverWithOldAPI() {
    }

    @PostConstruct
    public void init() {
        connector = Consumer.createJavaConsumerConnector(new ConsumerConfig(kafkaConfig.getConsumer()));
        executor = Executors.newFixedThreadPool(2, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                ThreadGroup group = new ThreadGroup("data-consumer-pool");
                Thread t = new Thread(group, r);
                return t;
            }
        });
    }

    /**
     * 接收消息，异常退出后只需重新启动该方法。
     * kafka消费者不是线程安全的
     */
    public void receiveMessage(StatusAware program) {
        closing = false;
        // 指定客户端读取主题
        Map<String, Integer> topicCountMap = new HashMap<>();
        topicCountMap.put(rtData, 1);
        topicCountMap.put(seatData, 1);

        // 获取该主题下的消息集合
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = connector.createMessageStreams(topicCountMap);

        //启动rt解析线程
        KafkaStream<byte[], byte[]> kafkaStream = consumerMap.get(rtData).get(0);
        final ConsumerIterator<byte[], byte[]> rt = kafkaStream.iterator();
        consume0(rt, rtData, program);

        //启动seat解析线程
        KafkaStream<byte[], byte[]> kafkaStream2 = consumerMap.get(seatData).get(0);
        final ConsumerIterator<byte[], byte[]> seat = kafkaStream2.iterator();
        consume0(seat, seatData, program);


    }

    private void consume0(final ConsumerIterator<byte[], byte[]> it, final String topic, final StatusAware program) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                logger.info("waiting for msg on topic: " + topic + ".");
                try {
                    while (it.hasNext() && !closing) {
                        MessageAndMetadata<byte[], byte[]> record = it.next();
                        if (record == null) continue;
                        String message = new String(record.message());

                        //parse and get lag info
                        String key = parseAndTrace(record);
                        Record r = new Record(record.topic(), key, message, record.offset());
                        try {
                            taskDefineService.cacheTask(r);
                        } catch (TaskRejectedException e) {
                            logger.error("async task queue is full !", e);
                        }
                        ReceiverWithOldAPI.this.offset = record.offset();

                    }
                } finally {
                    program.setStatus(RunningStatusMetric.RunningStatus.stopped);
                    logger.warn("stopped listening on topic:" + topic + ".");
                }

            }
        });
    }

    public String parseAndTrace(MessageAndMetadata<byte[], byte[]> record) {
        String key = "";
        if (record.key() != null) key = new String(record.key());
        // 打印获取到的消息
        Date now = new Date();
        //key current+"#"+time+"#"+key
        if (!key.contains("#")) {
            return key;
        }
        String idx = key.substring(0, key.indexOf('#'));
        int secondPos = key.indexOf('#', idx.length() + 1);
        key = key.substring(secondPos + 1);
        if (logger.isTraceEnabled()) {
            String time = key.substring(idx.length() + 1, secondPos);
            long lag = now.getTime() - Long.parseLong(time);
            logger.trace(String.format(
                    "topic:%s,idx:%s, lag:%dms, now:%s  offset = %d, key = %s, len of value = %d",
                    record.topic(), idx, lag, sdf.format(now), record.offset(), key, record.message().length));
        }
        return key;
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


    @PreDestroy
    public void stopGracefully() {
        this.closing = true;
        if (logger.isInfoEnabled()) {
            logger.info("consumer on topic " + rtData + "," + seatData + " is closing");
        }
        try {
            connector.shutdown();
        } catch (Exception e) {
        }
        try {
            executor.shutdown();
        } catch (Exception e) {
        }
    }

    public long getOffset() {
        return offset;
    }

    public static class Record {
        private String topic;
        private String key;
        private String value;
        private Long offset;

        public Record(String topic, String key, String value, Long offset) {
            this.topic = topic;
            this.key = key;
            this.value = value;
            this.offset = offset;
        }

        public String topic() {
            return topic;
        }

        public String key() {
            return key;
        }

        public String value() {
            return value;
        }

        public Long offset() {
            return offset;
        }
    }

    public static void main(String[] args) {

    }
}
