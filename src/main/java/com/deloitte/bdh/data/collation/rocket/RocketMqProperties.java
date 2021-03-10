package com.deloitte.bdh.data.collation.rocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;


import lombok.Data;

@Configuration
@Data
@RefreshScope
public class RocketMqProperties {
	
	
//	@Value("${rocket.mq.sync.http_endpoint}")
    protected String http_endpoint = "http://1841521559125813.mqrest.cn-qingdao-public.aliyuncs.com";
	
//	@Value("${rocket.mq.sync.access_key}")
    protected String access_key;
	
//	@Value("${rocket.mq.sync.secret_key}")
    protected String secret_key;
	
//	@Value("${rocket.mq.sync.topic}")
    protected String topic;
	
//	@Value("${rocket.mq.sync.group_id}")
    protected String group_id;
	
//	@Value("${rocket.mq.sync.instance_id}")
    protected String instance_id = "MQ_INST_1841521559125813_BXfjmWN7";

}
