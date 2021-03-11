//package com.deloitte.bdh.data.collation.mq.consumer;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Properties;
//
//import javax.annotation.Resource;
//
//import com.deloitte.bdh.data.collation.service.BiEvmFileConsumerService;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.apache.kafka.clients.consumer.ConsumerRecords;
//import org.apache.kafka.clients.consumer.KafkaConsumer;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//
//import com.deloitte.bdh.common.properties.BiProperties;
//import com.deloitte.bdh.common.util.JsonUtil;
//import com.deloitte.bdh.common.util.ThreadLocalHolder;
//import com.deloitte.bdh.data.analyse.service.EmailService;
//import com.deloitte.bdh.data.collation.enums.KafkaTypeEnum;
//import com.deloitte.bdh.data.collation.mq.KafkaMessage;
//import com.deloitte.bdh.data.collation.service.KafkaBiPlanService;
//
//import lombok.extern.slf4j.Slf4j;
//
//
//@Slf4j
//@Component
//public class BiConsumer implements ApplicationRunner {
//    @Resource
//    private BiProperties properties;
//
//    @Autowired
//    private KafkaBiPlanService kafkaBiPlanService;
//
//    @Resource
//    private EmailService emailService;
//
//    @Resource
//    private BiEvmFileConsumerService evmFileConsumerService;
//
//    @Override
//    public void run(ApplicationArguments args) {
//        KafkaConsumer<String, String> consumer = config();
//        while (true) {
//            try {
//                ConsumerRecords<String, String> records = consumer.poll(1000);
//                //必须在下次Poll之前消费完这些数据, 且总耗时不得超过SESSION_TIMEOUT_MS_CONFIG。
//                //建议开一个单独的线程池来消费消息，然后异步返回结果。
//                for (ConsumerRecord<String, String> record : records) {
//                    log.info("测试消费体：" + record.toString());
//                    KafkaMessage message = JsonUtil.string2Obj(record.value(), KafkaMessage.class);
//                    log.info("uuid:" + message.getUuid() + "   message：" + message.toString());
//                    if (null != message) {
//                        ThreadLocalHolder.async(message.getTenantCode(), message.getTenantId(), message.getOperator(), message::process);
//                        String beanName = message.getBeanName();
//                        log.info("uuid:" + message.getUuid() + "   beanname：" + beanName);
//                        switch (KafkaTypeEnum.valueOf(beanName)) {
//                            case Plan_start:
//
//                                log.info("uuid:" + message.getUuid() + " Plan_start body:" + message.getBody() + " start");
//                                kafkaBiPlanService.BiEtlSyncPlan(message);
//                                log.info("uuid:" + message.getUuid() + " Plan_start  end");
//                                break;
//                            case Plan_check_end:
//                                log.info("uuid:" + message.getUuid() + " Plan_check_end body:" + message.getBody() + " start");
//                                kafkaBiPlanService.BiEtlSyncManyPlan(message);
//                                log.info("uuid:" + message.getUuid() + " Plan_check_end  end");
//                                break;
//                            case Plan_checkMany_end:
//                                log.info("uuid:" + message.getUuid() + " Plan_checkMany_end body:" + message.getBody() + " start");
//                                kafkaBiPlanService.BiEtlSyncManyEndPlan(message);
//                                log.info("uuid:" + message.getUuid() + " Plan_checkMany_end  end");
//                                break;
//
//                            case EVM_FILE:
//                                evmFileConsumerService.consumer(message);
//                                break;
//
//                            default:
//                                log.error("uuid:" + message.getUuid() + " default：not catch beaname ");
//                                break;
//                        }
//                    }
//
//
//                }
//            } catch (Exception e) {
//                try {
//                    Thread.sleep(1000);
//                } catch (Throwable ignore) {
//                    log.error(e.getMessage());
//                }
//                log.error(e.getMessage());
//
//            }
//        }
//    }
//
//    private KafkaConsumer<String, String> config() {
//        //加载kafka.properties。
//        Properties props = new Properties();
//        //设置接入点，请通过控制台获取对应Topic的接入点。
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getKafkaServers());
//        //两次Poll之间的最大允许间隔。
//        //消费者超过该值没有返回心跳，服务端判断消费者处于非存活状态，服务端将消费者从Consumer Group移除并触发Rebalance，默认30s。
//        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
//        //每次Poll的最大数量。
//        //注意该值不要改得太大，如果Poll太多数据，而不能在下次Poll之前消费完，则会触发一次负载均衡，产生卡顿。
//        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 30);
//        props.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, 6291456);
//        //消息的反序列化方式。
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
//        //当前消费实例所属的消费组，请在控制台申请之后填写。
//        //属于同一个组的消费实例，会负载消费消息。
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, properties.getKafkaGroupId());
//        //构造消息对象，也即生成一个消费实例。
//        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
//        //设置消费组订阅的Topic，可以订阅多个。
//        //如果GROUP_ID_CONFIG是一样，则订阅的Topic也建议设置成一样。
//        List<String> subscribedTopics = new ArrayList<>();
//        //如果需要订阅多个Topic，则在这里添加进去即可。
//        //每个Topic需要先在控制台进行创建。
//        String topicStr = properties.getKafkaTopic();
//        String[] topics = topicStr.split(",");
//        for (String topic : topics) {
//            subscribedTopics.add(topic.trim());
//        }
//        consumer.subscribe(subscribedTopics);
//
//        return consumer;
//    }
//}
//
//
//
//
//
//
//
//
//
//
