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
@TableName("BI_COMPONENT_CONNECTION")
public class BiComponentConnection implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    /**
     * 编码
     */
    @TableField("CODE")
    private String code;

    /**
     * 关联COMPONENT编码
     */
    @TableField("FROM_COMPONENT_CODE")
    private String fromComponentCode;

    /**
     * 被关联COMPONENT编码
     */
    @TableField("TO_COMPONENT_CODE")
    private String toComponentCode;

    /**
     * 所属模板code
     */
    @TableField("REF_MODEL_CODE")
    private String refModelCode;

    /**
     * 版本号
     */
    @TableField("VERSION")
    private String version;

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

    public String getFromComponentCode() {
        return fromComponentCode;
    }

    public void setFromComponentCode(String fromComponentCode) {
        this.fromComponentCode = fromComponentCode;
    }

    public String getToComponentCode() {
        return toComponentCode;
    }

    public void setToComponentCode(String toComponentCode) {
        this.toComponentCode = toComponentCode;
    }

    public String getRefModelCode() {
        return refModelCode;
    }

    public void setRefModelCode(String refModelCode) {
        this.refModelCode = refModelCode;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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
        return "BiComponentConnection{" +
                "id=" + id +
                ", code=" + code +
                ", fromComponentCode=" + fromComponentCode +
                ", toComponentCode=" + toComponentCode +
                ", refModelCode=" + refModelCode +
                ", version=" + version +
                ", createDate=" + createDate +
                ", createUser=" + createUser +
                ", modifiedDate=" + modifiedDate +
                ", modifiedUser=" + modifiedUser +
                ", ip=" + ip +
                ", tenantId=" + tenantId +
                "}";
    }
}
