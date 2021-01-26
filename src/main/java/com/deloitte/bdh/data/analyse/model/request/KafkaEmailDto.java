package com.deloitte.bdh.data.analyse.model.request;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ApiModel("邮件发送请求参数封装kafka")
@ToString
@Getter
@Setter
public class KafkaEmailDto implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4076141363008669891L;
	private EmailDto emailDto;
	
	private UserIdMailDto userIdMailDto;
	
	private String pageId;

}
