package com.deloitte.bdh.common.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * oss属性配置
 *
 * @author pengdh
 * @date 2018/05/24
 */
@Configuration
@RefreshScope
public class OssProperties {

	@Value("${oss.endpoint}")
	private String ossEndpoint;
	@Value("${target.endpoint}")
	private String targetEndpoint;
	@Value("${replacement.endpoint}")
	private String replacementEndpoint;
	@Value("${oss.accessKeyId}")
	private String ossAccesskeyId;
	@Value("${oss.accessKeySecret}")
	private String ossAccesskeySecret;
	@Value("${oss.bucketName}")
	private String ossBucketName;
	@Value("${oss.filepath}")
	private String ossFilepath;



	public String getOssEndpoint() {
		return ossEndpoint;
	}

	public void setOssEndpoint(String ossEndpoint) {
		this.ossEndpoint = ossEndpoint;
	}

	public String getTargetEndpoint() {
		return targetEndpoint;
	}

	public void setTargetEndpoint(String targetEndpoint) {
		this.targetEndpoint = targetEndpoint;
	}

	public String getReplacementEndpoint() {
		return replacementEndpoint;
	}

	public void setReplacementEndpoint(String replacementEndpoint) {
		this.replacementEndpoint = replacementEndpoint;
	}

	public String getOssAccesskeyId() {
		return ossAccesskeyId;
	}

	public void setOssAccesskeyId(String ossAccesskeyId) {
		this.ossAccesskeyId = ossAccesskeyId;
	}

	public String getOssAccesskeySecret() {
		return ossAccesskeySecret;
	}

	public void setOssAccesskeySecret(String ossAccesskeySecret) {
		this.ossAccesskeySecret = ossAccesskeySecret;
	}

	public String getOssBucketName() {
		return ossBucketName;
	}

	public void setOssBucketName(String ossBucketName) {
		this.ossBucketName = ossBucketName;
	}

	public String getOssFilepath() {
		return ossFilepath;
	}

	public void setOssFilepath(String ossFilepath) {
		this.ossFilepath = ossFilepath;
	}

}
