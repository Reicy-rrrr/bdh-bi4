package com.deloitte.bdh.common.base;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author dahpeng
 * @date 2018/11/19
 */
@ApiModel(description = "接口请求基类")
public class RetRequest<T> implements Serializable {

    private static final long serialVersionUID = 5162177108958838841L;
    @ApiModelProperty(value = "请求标识号")
    private String sid;
    @ApiModelProperty(value = "当前操作用户id")
    @NotNull(message = " operator 不能为空")
    private String operator;
    @ApiModelProperty(value = "国际化语言标识", example = "cn")
    private String lang;
    @ApiModelProperty(value = "来源平台", allowableValues = "PC, IOS, Android", example = "PC")
    private String source;
    @ApiModelProperty(value = "来源平台版本号,PC 从 1.0开始, IOS, Android 传当前版本号", example = "1.0")
    private String version;
    @ApiModelProperty(value = "当前租户id")
    private String tenantId;
    @ApiModelProperty(value = "当前ip")
    private String ip;
    @ApiModelProperty(value = "请求对象数据")
    @Valid
    private T data;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}