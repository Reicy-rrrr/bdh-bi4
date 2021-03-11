package com.deloitte.bdh.common.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.aliyun.mq.http.MQConsumer;
import com.aliyun.mq.http.common.AckMessageException;
import com.aliyun.mq.http.model.Message;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.collation.enums.KafkaTypeEnum;
import com.deloitte.bdh.data.collation.mq.KafkaMessage;
import com.deloitte.bdh.data.collation.service.BiEvmFileConsumerService;
import com.deloitte.bdh.data.collation.service.KafkaBiPlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Author:LIJUN
 * Date:10/03/2021
 * Description:
 */
@Slf4j
@Component
public class SyncConsumer implements InitializingBean {

    @Resource
    private MQConsumer consumer;

    @Resource
    private KafkaBiPlanService kafkaBiPlanService;

    @Resource
    private BiEvmFileConsumerService evmFileConsumerService;

    @Resource
    private AsyncTaskExecutor executor;

    @Override
    public void afterPropertiesSet() {
        executor.execute(() -> {
            do {
                List<Message> messages = null;
                try {
                    // 长轮询消费消息
                    // 长轮询表示如果topic没有消息则请求会在服务端挂住3s，3s内如果有消息可以消费则立即返回
                    messages = consumer.consumeMessage(
                            3,// 一次最多消费3条(最多可设置为16条)
                            3// 长轮询时间3秒（最多可设置为30秒）
                    );
                } catch (Throwable e) {
                    log.error(e.getMessage());
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e1) {
                        log.error(e.getMessage());
                    }
                }
                // 没有消息
                if (messages == null || messages.isEmpty()) {
                    continue;
                }

                // 处理业务逻辑
                for (Message message : messages) {
                    log.info(message.getMessageBodyString());
                    log.info("Receive message: {}", message);
                    KafkaMessage kafkaMessage = JSON.parseObject(message.getMessageBodyString(), KafkaMessage.class);
                    ThreadLocalHolder.async(kafkaMessage.getTenantCode(), kafkaMessage.getTenantId(), kafkaMessage.getOperator(), () -> {
                    });
                    String beanName = kafkaMessage.getBeanName();
                    log.info("uuid:" + kafkaMessage.getUuid() + "   beanname：" + beanName);
                    switch (KafkaTypeEnum.valueOf(beanName)) {
                        case Plan_start:
                            log.info("uuid:" + kafkaMessage.getUuid() + " Plan_start body:" + kafkaMessage.getBody() + " start");
                            kafkaBiPlanService.BiEtlSyncPlan(kafkaMessage);
                            log.info("uuid:" + kafkaMessage.getUuid() + " Plan_start  end");
                            break;
                        case Plan_check_end:
                            log.info("uuid:" + kafkaMessage.getUuid() + " Plan_check_end body:" + kafkaMessage.getBody() + " start");
                            kafkaBiPlanService.BiEtlSyncManyPlan(kafkaMessage);
                            log.info("uuid:" + kafkaMessage.getUuid() + " Plan_check_end  end");
                            break;
                        case Plan_checkMany_end:
                            log.info("uuid:" + kafkaMessage.getUuid() + " Plan_checkMany_end body:" + kafkaMessage.getBody() + " start");
                            kafkaBiPlanService.BiEtlSyncManyEndPlan(kafkaMessage);
                            log.info("uuid:" + kafkaMessage.getUuid() + " Plan_checkMany_end  end");
                            break;

                        case EVM_FILE:
                            evmFileConsumerService.consumer(kafkaMessage);
                            break;
                        default:
                            log.error("uuid:" + kafkaMessage.getUuid() + " default：not catch beaname ");
                            break;
                    }
                }

                // Message.nextConsumeTime前若不确认消息消费成功，则消息会重复消费
                // 消息句柄有时间戳，同一条消息每次消费拿到的都不一样
                {
                    List<String> handles = new ArrayList<>();
                    for (Message message : messages) {
                        handles.add(message.getReceiptHandle());
                    }
                    try {
                        consumer.ackMessage(handles);
                    } catch (Throwable e) {
                        // 某些消息的句柄可能超时了会导致确认不成功
                        if (e instanceof AckMessageException) {
                            AckMessageException errors = (AckMessageException) e;
                            log.error("Ack message fail, requestId is: {}, fail handles:", errors.getRequestId());
                            if (errors.getErrorMessages() != null) {
                                for (String errorHandle : errors.getErrorMessages().keySet()) {
                                    log.error("Handle: {}, ErrorCode: {}, ErrorMsg: {}", errorHandle,
                                            errors.getErrorMessages().get(errorHandle).getErrorCode(),
                                            errors.getErrorMessages().get(errorHandle).getErrorMessage());
                                }
                            }
                            continue;
                        }
                        log.error(e.getMessage());
                    }
                }
            } while (true);
        });

    }
}
