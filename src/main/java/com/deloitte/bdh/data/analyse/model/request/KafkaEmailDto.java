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
@Setter
@Getter
@ToString
public class KafkaEmailDto implements Serializable{
	
/**
	 * 
	 */
	private static final long serialVersionUID = 45096208883792438L;

	@ApiModelProperty(value = "接收方邮件")
	private String email;
	@ApiModelProperty(value = "抄送人")
	private List<String> ccList;
	@ApiModelProperty("主题")
	private String subject;
	@ApiModelProperty("邮件内容")
	private String content;
	@ApiModelProperty("模板")
	private String template;
	@ApiModelProperty("自定义参数")
	private HashMap<String, Object> paramMap;
	
	private String pageId;

}
