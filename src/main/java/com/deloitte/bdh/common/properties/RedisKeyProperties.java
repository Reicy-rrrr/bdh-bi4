package com.deloitte.bdh.common.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * rediskey配置类
 *
 * @author pengdh
 * @date 2018/05/17
 */
@Component
@ConfigurationProperties(prefix = "portal.key")
@PropertySource("classpath:redisKey.properties")
public class RedisKeyProperties {

	private String userIdPrefix;
	private String resourceIdPrefix;
	private String biTargetKey;
	@Value(value = "${portal.key.admin.enumCode}")
	private String enumCode;

	public String getUserIdPrefix() {
		return userIdPrefix;
	}

	public void setUserIdPrefix(String userIdPrefix) {
		this.userIdPrefix = userIdPrefix;
	}

	public String getResourceIdPrefix() {
		return resourceIdPrefix;
	}

	public void setResourceIdPrefix(String resourceIdPrefix) {
		this.resourceIdPrefix = resourceIdPrefix;
	}

	public String getBiTargetKey() {
		return biTargetKey;
	}

	public void setBiTargetKey(String biTargetKey) {
		this.biTargetKey = biTargetKey;
	}

	public String getEnumCode() {
		return enumCode;
	}

	public void setEnumCode(String enumCode) {
		this.enumCode = enumCode;
	}
}
