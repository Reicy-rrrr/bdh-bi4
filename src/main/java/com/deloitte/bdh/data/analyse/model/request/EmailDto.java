package com.deloitte.bdh.data.analyse.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * @author dahpeng
 * @date 2019/07/18
 */
@ApiModel("邮件发送请求参数封装")
@ToString
public class EmailDto implements Serializable {

	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "接收方邮件", required = true)
	private String email;
	@ApiModelProperty(value = "抄送人", required = true)
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<String> getCcList() {
		return ccList;
	}

	public void setCcList(List<String> ccList) {
		this.ccList = ccList;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public HashMap<String, Object> getParamMap() {
		return paramMap;
	}

	public void setParamMap(HashMap<String, Object> paramMap) {
		this.paramMap = paramMap;
	}

	public String getPageId() {
		return pageId;
	}

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	
}
