package com.deloitte.bdh.common.mq;

import com.aliyun.mq.http.MQClient;
import com.aliyun.mq.http.MQConsumer;
import com.aliyun.mq.http.MQProducer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * Author:LIJUN
 * Date:10/03/2021
 * Description:
 */
@Configuration
public class ProducerClient {

    @Resource
    private MQConfig mqConfig;

    @Bean(destroyMethod = "close")
    public MQClient buildMqClient() {
        return new MQClient(
                // 设置HTTP接入域名（此处以公共云生产环境为例）
                mqConfig.getNameSrvAddr(),
                // AccessKey 阿里云身份验证，在阿里云服务器管理控制台创建
                mqConfig.getAccessKey(),
                // SecretKey 阿里云身份验证，在阿里云服务器管理控制台创建
                mqConfig.getSecretKey()
        );
    }

    @Bean
    public MQProducer buildProducer() {
        MQProducer producer;
        if (StringUtils.isNotBlank(mqConfig.getGroupId())) {
            producer = buildMqClient().getProducer(mqConfig.getInstanceId(), mqConfig.getTopic());
        } else {
            producer = buildMqClient().getProducer(mqConfig.getTopic());
        }
        return producer;
    }

    @Bean
    public MQConsumer buildConsumer() {
        final MQConsumer consumer;
        if (StringUtils.isNotBlank(mqConfig.getGroupId())) {
            consumer = buildMqClient().getConsumer(mqConfig.getInstanceId(), mqConfig.getTopic(), mqConfig.getGroupId(), null);
        } else {
            consumer = buildMqClient().getConsumer(mqConfig.getTopic(), mqConfig.getGroupId());
        }
        return consumer;
    }

}
