package com.deloitte.bdh.common.mq;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * Author:LIJUN
 * Date:10/03/2021
 * Description:
 */
@Configuration
@Data
@RefreshScope
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

    @Value("${rocketmq.tag.email}")
    private String tagEmail;

    @Value("${rocketmq.tag.sync}")
    private String tagSync;

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

    public String getTagEmail() {
        return tagEmail;
    }

    public void setTagEmail(String tagEmail) {
        this.tagEmail = tagEmail;
    }

    public String getTagSync() {
        return tagSync;
    }

    public void setTagSync(String tagSync) {
        this.tagSync = tagSync;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
}
