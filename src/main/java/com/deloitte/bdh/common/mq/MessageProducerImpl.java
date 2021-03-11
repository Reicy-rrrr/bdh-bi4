package com.deloitte.bdh.common.mq;

import com.aliyun.mq.http.MQProducer;
import com.aliyun.mq.http.model.TopicMessage;
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

    @Override
    public void send(String body, String messageKey) {
        try {
            TopicMessage pubMsg = new TopicMessage(body.getBytes(), mqConfig.getTag());
            // 设置KEY
            pubMsg.setMessageKey(messageKey);
            // 同步发送消息，只要不抛异常就是成功
            TopicMessage pubResultMsg = producer.publishMessage(pubMsg);
            // 同步发送消息，只要不抛异常就是成功
            log.info("Send mq message success. Topic is: {}, msgId is: {}", mqConfig.getTopic(), pubResultMsg.getMessageId());
        } catch (Throwable e) {
            e.printStackTrace();
            // 消息发送失败，可在此做补偿处理
            log.error("Send mq message failed. Topic is:" + mqConfig.getTopic());
            log.error(e.getMessage());
        }
    }
}
