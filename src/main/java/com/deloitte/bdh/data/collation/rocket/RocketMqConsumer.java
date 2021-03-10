package com.deloitte.bdh.data.collation.rocket;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


import lombok.extern.slf4j.Slf4j;
import com.aliyun.mq.http.MQClient;
import com.aliyun.mq.http.MQConsumer;
import com.aliyun.mq.http.common.AckMessageException;
import com.aliyun.mq.http.model.Message;
import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.collation.enums.KafkaTypeEnum;
import com.deloitte.bdh.data.collation.mq.KafkaMessage;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

@Slf4j
@Component
public class RocketMqConsumer implements ApplicationRunner{
	
	@Resource
	private RocketMqProperties rocketMqProperties;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		MQClient mqClient = new MQClient(
                // 设置HTTP接入域名（此处以公共云生产环境为例）
				rocketMqProperties.getHttp_endpoint(),
                // AccessKey 阿里云身份验证，在阿里云服务器管理控制台创建
				rocketMqProperties.getAccess_key(),
                // SecretKey 阿里云身份验证，在阿里云服务器管理控制台创建
				rocketMqProperties.getSecret_key()
        );

        // 所属的 Topic
        final String topic = rocketMqProperties.getTopic();
        // 您在控制台创建的 Consumer ID(Group ID)
        final String groupId = rocketMqProperties.getGroup_id();
        // Topic所属实例ID，默认实例为空
        final String instanceId = rocketMqProperties.getInstance_id();

        final MQConsumer consumer;
        if (instanceId != null && instanceId != "") {
            consumer = mqClient.getConsumer(instanceId, topic, groupId, null);
        } else {
            consumer = mqClient.getConsumer(topic, groupId);
        }

        // 在当前线程循环消费消息，建议是多开个几个线程并发消费消息
        do {
            List<Message> messages = null;

            try {
                // 长轮询顺序消费消息, 拿到的消息可能是多个分区的（对于分区顺序）一个分区的内的消息一定是顺序的
                // 对于顺序消费，如果一个分区内的消息只要有没有被确认消费成功的，则对于这个分区下次还会消费到相同的消息
                // 对于一个分区，只有所有消息确认消费成功才能消费下一批消息
                // 长轮询表示如果topic没有消息则请求会在服务端挂住3s，3s内如果有消息可以消费则立即返回
                messages = consumer.consumeMessageOrderly(
                        3,// 一次最多消费3条(最多可设置为16条)
                        3// 长轮询时间3秒（最多可设置为30秒）
                );
            } catch (Throwable e) {
                e.printStackTrace();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            // 没有消息
            if (messages == null || messages.isEmpty()) {
                log.error(Thread.currentThread().getName() + ": no new message, continue!");
                continue;
            }

            // 处理业务逻辑
            log.info("Receive " + messages.size() + " messages:");
            for (Message message : messages) {
            	log.info(message.toString());
            	log.info("ShardingKey: " + message.getShardingKey() + ",Properties" + message.getProperties().get(message.getShardingKey()));
            	byte[] messageBody = message.getMessageBodyBytes();
            	if(null != messageBody) {
            		RocketMqMessage messageRocket = JsonUtil.string2Obj(new String(messageBody), RocketMqMessage.class);
                    log.info("uuid:" + messageRocket.getUuid() + "   message：" + messageRocket.toString());
                    if (null != message) {
                        ThreadLocalHolder.async(messageRocket.getTenantCode(), messageRocket.getTenantId(), messageRocket.getOperator(), messageRocket::process);
                        String beanName = messageRocket.getBeanName();
                        log.info("uuid:" + messageRocket.getUuid() + "   beanname：" + beanName);
                        switch (KafkaTypeEnum.valueOf(beanName)) {
                            case Plan_start:

                                log.info("uuid:" + messageRocket.getUuid() + " Plan_start body:" + messageRocket.getBody() + " start");
//                                kafkaBiPlanService.BiEtlSyncPlan(message);
                                log.info("uuid:" + messageRocket.getUuid() + " Plan_start  end");
                                break;
                            case Plan_check_end:
                                log.info("uuid:" + messageRocket.getUuid() + " Plan_check_end body:" + messageRocket.getBody() + " start");
//                                kafkaBiPlanService.BiEtlSyncManyPlan(message);
                                log.info("uuid:" + messageRocket.getUuid() + " Plan_check_end  end");
                                break;
                            case Plan_checkMany_end:
                                log.info("uuid:" + messageRocket.getUuid() + " Plan_checkMany_end body:" + messageRocket.getBody() + " start");
//                                kafkaBiPlanService.BiEtlSyncManyEndPlan(message);
                                log.info("uuid:" + messageRocket.getUuid() + " Plan_checkMany_end  end");
                                break;

                            case EVM_FILE:
//                                evmFileConsumerService.consumer(message);
                                break;

                            default:
                                log.error("uuid:" + messageRocket.getUuid() + " default：not catch beaname ");
                                break;
                        }
                    }
            		
            	}else {
            		log.error("ShardingKey: " + message.getShardingKey() + ",Properties" + message.getProperties().get(message.getShardingKey()));
            		log.error("tag: " + message.getMessageTag() + " messages  is null");
            		continue;
            	}
            }

            // Message.nextConsumeTime前若不确认消息消费成功，则消息会重复消费
            // 消息句柄有时间戳，同一条消息每次消费拿到的都不一样
            {
                List<String> handles = new ArrayList<String>();
                for (Message message : messages) {
                    handles.add(message.getReceiptHandle());
                }

                try {
                    consumer.ackMessage(handles);
                } catch (Throwable e) {
                    // 某些消息的句柄可能超时了会导致确认不成功
                    if (e instanceof AckMessageException) {
                        AckMessageException errors = (AckMessageException) e;
                        log.error("Ack message fail, requestId is:" + errors.getRequestId() + ", fail handles:");
                        if (errors.getErrorMessages() != null) {
                            for (String errorHandle :errors.getErrorMessages().keySet()) {
                            	log.error("Handle:" + errorHandle + ", ErrorCode:" + errors.getErrorMessages().get(errorHandle).getErrorCode()
                                        + ", ErrorMsg:" + errors.getErrorMessages().get(errorHandle).getErrorMessage());
                            }
                        }
                        continue;
                    }
                    e.printStackTrace();
                }
            }
        } while (true);
		
	}
	
	
	
	

}
