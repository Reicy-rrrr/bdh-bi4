package com.deloitte.bdh.common.mq;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Author:LIJUN
 * Date:10/03/2021
 * Description:
 */
@Configuration
public class MQConfig {

    @Value("${rocketmq.accessKey}")
    private String accessKey;

    @Value("${rocketmq.secretKey}")
    private String secretKey;

    @Value("${rocketmq.nameSrvAddr}")
    private String nameSrvAddr;

    @Value("${rocketmq.topic}")
    private String topic;

    @Value("${rocketmq.groupId}")
    private String groupId;

    @Value("${rocketmq.tag}")
    private String tag;

    @Value("${rocketmq.instanceId}")
    private String instanceId;

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getNameSrvAddr() {
        return nameSrvAddr;
    }

    public void setNameSrvAddr(String nameSrvAddr) {
        this.nameSrvAddr = nameSrvAddr;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
}
