package com.deloitte.bdh.common.mq;

import com.alibaba.fastjson.JSON;
import com.aliyun.mq.http.MQProducer;
import com.aliyun.mq.http.model.TopicMessage;
import com.deloitte.bdh.common.util.SnowFlakeUtil;
import com.deloitte.bdh.data.collation.mq.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Author:LIJUN
 * Date:10/03/2021
 * Description:
 */
@Service
@Slf4j
public class MessageProducerImpl implements MessageProducer {

    @Resource
    private MQProducer producer;

    @Resource
    private MQConfig mqConfig;

    private static SnowFlakeUtil idWorker = new SnowFlakeUtil(0, 0);

    @Override
    public void sendEmailMessage(String body) {
        try {
            TopicMessage pubMsg = new TopicMessage(body.getBytes(), mqConfig.getTagEmail());
            // 设置KEY
            pubMsg.setMessageKey(idWorker.nextId() + "");
            // 同步发送消息，只要不抛异常就是成功
            TopicMessage pubResultMsg = producer.publishMessage(pubMsg);
            // 同步发送消息，只要不抛异常就是成功
            log.info("Send email message success. Topic is: {}, msgId is: {}", mqConfig.getTopic(), pubResultMsg.getMessageId());
        } catch (Throwable e) {
            e.printStackTrace();
            // 消息发送失败，可在此做补偿处理
            log.error("Send email message failed. Topic is:" + mqConfig.getTopic());
            log.error(e.getMessage());
        }
    }

    @Override
    public void sendSyncMessage(KafkaMessage message) {
        try {
            TopicMessage pubMsg = new TopicMessage(JSON.toJSONString(message).getBytes(), mqConfig.getTagSync());
            // 定时消息, 定时时间为5s后
            pubMsg.setStartDeliverTime(System.currentTimeMillis() + 5 * 1000);
            // 设置KEY
            pubMsg.setMessageKey(idWorker.nextId() + "");
            // 同步发送消息，只要不抛异常就是成功
            TopicMessage pubResultMsg = producer.publishMessage(pubMsg);
            // 同步发送消息，只要不抛异常就是成功
            log.info("Send sync message success. Topic is: {}, msgId is: {}", mqConfig.getTopic(), pubResultMsg.getMessageId());
        } catch (Throwable e) {
            e.printStackTrace();
            // 消息发送失败，可在此做补偿处理
            log.error("Send sync message failed. Topic is:" + mqConfig.getTopic());
            log.error(e.getMessage());
        }
    }
}
