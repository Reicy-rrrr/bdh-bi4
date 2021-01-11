package com.deloitte.bdh.data.collation.service.impl;

import com.deloitte.bdh.common.properties.BiProperties;
import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.data.collation.service.KafkaTestService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;

@Slf4j
@Service
public class KafkaTestServiceImpl implements KafkaTestService {

    @Resource
    private BiProperties properties;

    @Override
    public void send(String str) {
        //加载kafka.properties。
        //设置接入点，请通过控制台获取对应Topic的接入点。
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
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);

        //构造一个Kafka消息。
        String topic = properties.getKafkaTopic(); //消息所属的Topic，请在控制台申请之后，填写在这里。
        log.info("配置{}完成，开始发送消息", JsonUtil.obj2String(props));
        try {
            //批量获取Future对象可以加快速度,。但注意，批量不要太大。
            List<Future<RecordMetadata>> futures = new ArrayList<Future<RecordMetadata>>(128);
            for (int i = 0; i < 100; i++) {
                //发送消息，并获得一个Future对象。
                ProducerRecord<String, String> kafkaMessage = new ProducerRecord<String, String>(topic, str + ": " + i);
                Future<RecordMetadata> metadataFuture = producer.send(kafkaMessage);
                futures.add(metadataFuture);

            }
            producer.flush();
            for (Future<RecordMetadata> future : futures) {
                //同步获得Future对象的结果。
                try {
                    RecordMetadata recordMetadata = future.get();
                    System.out.println("Produce ok:" + recordMetadata.toString());
                } catch (Throwable t) {
                    log.error("error occurred");
                    t.printStackTrace();
                }
            }
        } catch (Exception e) {
            //客户端内部重试之后，仍然发送失败，业务要应对此类错误。
            log.error("error occurred");
            e.printStackTrace();
        }
    }

}
