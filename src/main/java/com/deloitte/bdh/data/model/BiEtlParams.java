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
 * @since 2020-09-25
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
    @TableField("KEY")
    private String key;

    /**
     * 参数value
     */
    @TableField("VALUE")
    private String value;

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
    @TableField("RELATE_CODE")
    private String relateCode;

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
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
    public String getRelateCode() {
        return relateCode;
    }

    public void setRelateCode(String relateCode) {
        this.relateCode = relateCode;
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

    @Override
    public String toString() {
        return "BiEtlParams{" +
        "id=" + id +
        ", code=" + code +
        ", name=" + name +
        ", key=" + key +
        ", value=" + value +
        ", paramsGroup=" + paramsGroup +
        ", paramsComponent=" + paramsComponent +
        ", relateCode=" + relateCode +
        ", createDate=" + createDate +
        ", createUser=" + createUser +
        ", modifiedDate=" + modifiedDate +
        ", modifiedUser=" + modifiedUser +
        ", ip=" + ip +
        ", tenantId=" + tenantId +
        "}";
    }
}
