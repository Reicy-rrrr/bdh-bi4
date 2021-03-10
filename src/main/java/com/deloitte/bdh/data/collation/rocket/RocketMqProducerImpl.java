package com.deloitte.bdh.data.collation.rocket;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.aliyun.mq.http.MQClient;
import com.aliyun.mq.http.MQProducer;
import com.aliyun.mq.http.model.TopicMessage;
import com.deloitte.bdh.common.util.JsonUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RocketMqProducerImpl implements RocketMqProducer{

	@Resource
	private RocketMqProperties rocketMqProperties;

	public void sendRocket(RocketMqMessage message) {
		MQClient mqClient = new MQClient(
				// 设置HTTP接入域名（此处以公共云生产环境为例）
				rocketMqProperties.getHttp_endpoint(),
				// AccessKey 阿里云身份验证，在阿里云服务器管理控制台创建
				rocketMqProperties.getAccess_key(),
				// SecretKey 阿里云身份验证，在阿里云服务器管理控制台创建
				rocketMqProperties.getSecret_key());

		// 所属的 Topic
		final String topic = rocketMqProperties.getTopic();
		// Topic所属实例ID，默认实例为空
		final String instanceId = rocketMqProperties.getInstance_id();

		// 获取Topic的生产者
		MQProducer producer;
		if (instanceId != null && instanceId != "") {
			producer = mqClient.getProducer(instanceId, topic);
		} else {
			producer = mqClient.getProducer(topic);
		}

		try {
			// 循环发送8条消息

			String key = message.getKey();
			TopicMessage pubMsg = new TopicMessage(
					// 消息内容
					JsonUtil.obj2String(message).getBytes(),
					// 消息标签
					key);
			// 设置顺序消息的分区KEY
			pubMsg.setShardingKey(key);
			pubMsg.getProperties().put(key, message.getUuid());
			// 同步发送消息，只要不抛异常就是成功
			TopicMessage pubResultMsg = producer.publishMessage(pubMsg);

			// 同步发送消息，只要不抛异常就是成功
			log.info(new Date() + " Send mq message success. Topic is:" + topic + ", msgId is: "
					+ pubResultMsg.getMessageId() + ", bodyMD5 is: " + pubResultMsg.getMessageBodyMD5());

		} catch (Throwable e) {
			// 消息发送失败，需要进行重试处理，可重新发送这条消息或持久化这条数据进行补偿处理
			log.error(new Date() + " Send mq message failed. Topic is:" + topic);
			e.printStackTrace();
		}

		mqClient.close();
	}

}
