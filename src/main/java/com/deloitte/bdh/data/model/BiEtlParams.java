package com.deloitte.bdh.data.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author lw
 * @since 2020-10-01
 */
@TableName("BI_ETL_PARAMS")
public class BiEtlParams implements Serializable {

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
     * 参数所属组类型（properties、setting等）
     */
    @TableField("PARAMS_GROUP")
    private String paramsGroup;

    /**
     * 参数所属组件类型（processor、connecttion等等）
     */
    @TableField("PARAMS_COMPONENT")
    private String paramsComponent;

    /**
     * 关联的编码（processor、connecttion等等）
     */
    @TableField("REL_CODE")
    private String relCode;

    /**
     * 关联的processors 编码
     */
    @TableField("REL_PROCESSORS_CODE")
    private String relProcessorsCode;

    @TableField("CREATE_DATE")
    private LocalDateTime createDate;

    @TableField("CREATE_USER")
    private String createUser;

    @TableField("MODIFIED_DATE")
    private LocalDateTime modifiedDate;

    @TableField("MODIFIED_USER")
    private String modifiedUser;

    @TableField("IP")
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
    public String getParamsGroup() {
        return paramsGroup;
    }

    public void setParamsGroup(String paramsGroup) {
        this.paramsGroup = paramsGroup;
    }
    public String getParamsComponent() {
        return paramsComponent;
    }

    public void setParamsComponent(String paramsComponent) {
        this.paramsComponent = paramsComponent;
    }
    public String getRelCode() {
        return relCode;
    }

    public void setRelCode(String relCode) {
        this.relCode = relCode;
    }
    public String getRelProcessorsCode() {
        return relProcessorsCode;
    }

    public void setRelProcessorsCode(String relProcessorsCode) {
        this.relProcessorsCode = relProcessorsCode;
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
        return "BiEtlParams{" +
        "id=" + id +
        ", code=" + code +
        ", name=" + name +
        ", paramKey=" + paramKey +
        ", paramValue=" + paramValue +
        ", paramsGroup=" + paramsGroup +
        ", paramsComponent=" + paramsComponent +
        ", relCode=" + relCode +
        ", relProcessorsCode=" + relProcessorsCode +
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
