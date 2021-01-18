package com.deloitte.bdh.data.collation.model;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author lw
 * @since 2020-10-26
 */
@TableName("BI_COMPONENT_PARAMS")
public class BiComponentParams implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    /**
     * 编码
     */
    @TableField("CODE")
    private String code;

    /**
     * 参数中文名
     */
    @TableField("NAME")
    private String name;

    /**
     * 参数KEY
     */
    @TableField("PARAM_KEY")
    private String paramKey;

    /**
     * 参数value
     */
    @TableField("PARAM_VALUE")
    private String paramValue;

    /**
     * 关联的模型编码
     */
    @TableField("REF_MODEL_CODE")
    private String refModelCode;

    /**
     * 关联的COMPONENT编码
     */
    @TableField("REF_COMPONENT_CODE")
    private String refComponentCode;

    @TableField(value = "CREATE_DATE", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime createDate;

    @TableField(value = "CREATE_USER", fill = FieldFill.INSERT_UPDATE)
    private String createUser;

    @TableField(value = "MODIFIED_DATE", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime modifiedDate;

    @TableField(value = "MODIFIED_USER", fill = FieldFill.INSERT_UPDATE)
    private String modifiedUser;

    @TableField(value = "IP", fill = FieldFill.INSERT_UPDATE)
    private String ip;

    @TableField("TENANT_ID")
    private String tenantId;

    /**
     * 上级编码
     */
    @TableField("PARENT_CODE")
    private String parentCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParamKey() {
        return paramKey;
    }

    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }
    public String getRefModelCode() {
        return refModelCode;
    }

    public void setRefModelCode(String refModelCode) {
        this.refModelCode = refModelCode;
    }
    public String getRefComponentCode() {
        return refComponentCode;
    }

    public void setRefComponentCode(String refComponentCode) {
        this.refComponentCode = refComponentCode;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getModifiedUser() {
        return modifiedUser;
    }

    public void setModifiedUser(String modifiedUser) {
        this.modifiedUser = modifiedUser;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    @Override
    public String toString() {
        return "BiComponentParams{" +
        "id=" + id +
        ", code=" + code +
        ", name=" + name +
        ", paramKey=" + paramKey +
        ", paramValue=" + paramValue +
        ", refModelCode=" + refModelCode +
        ", refComponentCode=" + refComponentCode +
        ", createDate=" + createDate +
        ", createUser=" + createUser +
        ", modifiedDate=" + modifiedDate +
        ", modifiedUser=" + modifiedUser +
        ", ip=" + ip +
        ", tenantId=" + tenantId +
        ", parentCode=" + parentCode +
        "}";
    }
}
