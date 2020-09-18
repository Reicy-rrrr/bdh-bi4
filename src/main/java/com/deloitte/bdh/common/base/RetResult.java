package com.deloitte.bdh.common.base;

import com.alibaba.fastjson.JSON;
import com.deloitte.bdh.common.constant.CommonConstant;
import com.deloitte.bdh.common.util.AesUtil;
import com.deloitte.bdh.common.util.NetworkUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.net.SocketException;
import java.time.LocalDateTime;

/**
 * @author dahpeng
 * @Description: 返回对象实体
 * @date 2018/6/11
 */
@ApiModel(description = "返回响应数据")
public class RetResult<T> implements Serializable {

	private static final long serialVersionUID = 3758864789222317092L;

	@ApiModelProperty(value = "响应编码")
	public int code;
	@ApiModelProperty(value = "成功标志")
	private boolean success;
	@ApiModelProperty(value = "响应信息描述")
	private String message;
	@ApiModelProperty(value = "请求业务id")
	private String traceId;
	@ApiModelProperty(value = "服务器信息")
	private String host;
	@ApiModelProperty(value = "服务器时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime timestamp;
	@ApiModelProperty(value = "返回对象数据")
	private T data;

	public RetResult<T> setCommon() {
		this.timestamp = LocalDateTime.now();
		try {
			this.host = AesUtil.encrypt(NetworkUtil.getIPv4(), CommonConstant.AES_TOKEN);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return this;
	}

	public boolean isSuccess() {
		return success;
	}

	public RetResult<T> setSuccess(boolean success) {
		this.success = success;
		return this;
	}

	public int getCode() {
		return code;
	}

	public RetResult<T> setCode(RetCode retCode) {
		this.code = retCode.code;
		return this;
	}

	public RetResult<T> setCode(int code) {
		this.code = code;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public RetResult<T> setMessage(String message) {
		this.message = message;
		return this;
	}

	public T getData() {
		return data;
	}

	public RetResult<T> setData(T data) {
		this.data = data;
		return this;
	}

	public RetResult<T> success() {
		this.setCommon();
		success(null);
		return this;
	}

	public RetResult<T> success(T data) {
		this.setCommon();
		this.setSuccess(true);
		this.setCode(RetCode.SUCCESS);
		this.data = data;
		return this;
	}

	public RetResult<T> fail(int code, String message) {
		this.setCommon();
		this.setSuccess(false);
		this.setCode(code);
		this.setMessage(message);
		return this;
	}

	public RetResult<T> fail(int code) {
		fail(code, null);
		return this;
	}

	public String getTraceId() {
		return traceId;
	}

	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}

	public String getHost() {
		return host;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}