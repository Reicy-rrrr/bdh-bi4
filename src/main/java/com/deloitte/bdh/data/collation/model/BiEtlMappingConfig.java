package com.deloitte.bdh.data.collation.model;

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
 * @since 2020-10-26
 */
@TableName("BI_ETL_MAPPING_CONFIG")
public class BiEtlMappingConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 编码
     */
    @TableField("CODE")
    private String code;

    /**
     * 关联表编码
     */
    @TableField("REF_CODE")
    private String refCode;

    /**
     * 同步方式（0：直连，1：全量，2：增量）
     */
    @TableField("TYPE")
    private String type;

    /**
     * 偏移字段
     */
    @TableField("OFFSET_FIELD")
    private String offsetField;

    /**
     * 偏移量
     */
    @TableField("OFFSET_VALUE")
    private String offsetValue;

    /**
     * 同步的数据源id
     */
    @TableField("REL_SOURCE_ID")
    private String relSourceId;

    /**
     * 源表名
     */
    @TableField("FROM_TABLE_NAME")
    private String fromTableName;

    /**
     * 目标的表名
     */
    @TableField("TO_TABLE_NAME")
    private String toTableName;

    /**
     * 关联的processors 编码
     */
    @TableField("REL_PROCESSORS_CODE")
    private String relProcessorsCode;

    /**
     * 同步状态
     */
    @TableField("STATUS")
    private String status;

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
    public String getRefCode() {
        return refCode;
    }

    public void setRefCode(String refCode) {
        this.refCode = refCode;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getOffsetField() {
        return offsetField;
    }

    public void setOffsetField(String offsetField) {
        this.offsetField = offsetField;
    }
    public String getOffsetValue() {
        return offsetValue;
    }

    public void setOffsetValue(String offsetValue) {
        this.offsetValue = offsetValue;
    }
    public String getRelSourceId() {
        return relSourceId;
    }

    public void setRelSourceId(String relSourceId) {
        this.relSourceId = relSourceId;
    }
    public String getFromTableName() {
        return fromTableName;
    }

    public void setFromTableName(String fromTableName) {
        this.fromTableName = fromTableName;
    }
    public String getToTableName() {
        return toTableName;
    }

    public void setToTableName(String toTableName) {
        this.toTableName = toTableName;
    }
    public String getRelProcessorsCode() {
        return relProcessorsCode;
    }

    public void setRelProcessorsCode(String relProcessorsCode) {
        this.relProcessorsCode = relProcessorsCode;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
        return "BiEtlMappingConfig{" +
        "id=" + id +
        ", code=" + code +
        ", refCode=" + refCode +
        ", type=" + type +
        ", offsetField=" + offsetField +
        ", offsetValue=" + offsetValue +
        ", relSourceId=" + relSourceId +
        ", fromTableName=" + fromTableName +
        ", toTableName=" + toTableName +
        ", relProcessorsCode=" + relProcessorsCode +
        ", status=" + status +
        ", createDate=" + createDate +
        ", createUser=" + createUser +
        ", modifiedDate=" + modifiedDate +
        ", modifiedUser=" + modifiedUser +
        ", ip=" + ip +
        ", tenantId=" + tenantId +
        "}";
    }
}
