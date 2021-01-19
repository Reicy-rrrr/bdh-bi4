package com.deloitte.bdh.data.collation.service.impl;

import java.util.Properties;

import javax.annotation.Resource;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.stereotype.Service;

import com.deloitte.bdh.common.properties.BiProperties;
import com.deloitte.bdh.data.collation.mq.KafkaMessage;
import com.deloitte.bdh.data.collation.service.Producter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KafkaProducterImpl implements Producter {
    @Resource
    private BiProperties properties;
    

    @Override
    public void send(KafkaMessage message) {
//        try {
//            KafkaProducer<String, String> producer = init();
//            String key = message.getKey();
//            ProducerRecord<String, String> kafkaMessage = new ProducerRecord<>(properties.getKafkaTopic(), key, JsonUtil.obj2String(message));
//            Future<RecordMetadata> future = producer.send(kafkaMessage);
//            producer.flush();
//            //同步获得Future对象的结果。
//            try {
//                RecordMetadata recordMetadata = future.get();
//                log.info(recordMetadata.toString());
//            } catch (Throwable t) {
//                log.error("error occurred");
//                t.printStackTrace();
//            }
//        } catch (Exception e) {
//            //客户端内部重试之后，仍然发送失败，业务要应对此类错误。
//            log.error("error occurred", e);
//            throw new RuntimeException("error occurred");
//        }
    }


    private KafkaProducer<String, String> init() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getKafkaServers());
        //Kafka消息的序列化方式。
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        //请求的最长等待时间。
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 30 * 1000);
        //设置客户端内部重试次数。
        props.put(ProducerConfig.RETRIES_CONFIG, 5);
        //设置客户端内部重试间隔。
        props.put(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, 3000);
        //构造Producer对象，注意，该对象是线程安全的，一般来说，一个进程内一个Producer对象即可。
        //如果想提高性能，可以多构造几个对象，但不要太多，最好不要超过5个。
        return new KafkaProducer<>(props);
    }
}
