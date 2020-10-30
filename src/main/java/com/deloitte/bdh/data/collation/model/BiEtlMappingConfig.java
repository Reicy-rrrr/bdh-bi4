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
     * 本地总条数
     */
    @TableField("LOCAL_COUNT")
    private String localCount;

    /**
     * 同步的数据源id
     */
    @TableField("REF_SOURCE_ID")
    private String refSourceId;

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
     * 归属COMPONENTCODE
     */
    @TableField("REF_COMPONENT_CODE")
    private String refComponentCode;

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
    public String getLocalCount() {
        return localCount;
    }

    public void setLocalCount(String localCount) {
        this.localCount = localCount;
    }
    public String getRefSourceId() {
        return refSourceId;
    }

    public void setRefSourceId(String refSourceId) {
        this.refSourceId = refSourceId;
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

    @Override
    public String toString() {
        return "BiEtlMappingConfig{" +
        "id=" + id +
        ", code=" + code +
        ", refCode=" + refCode +
        ", type=" + type +
        ", offsetField=" + offsetField +
        ", offsetValue=" + offsetValue +
        ", localCount=" + localCount +
        ", refSourceId=" + refSourceId +
        ", fromTableName=" + fromTableName +
        ", toTableName=" + toTableName +
        ", refComponentCode=" + refComponentCode +
        ", createDate=" + createDate +
        ", createUser=" + createUser +
        ", modifiedDate=" + modifiedDate +
        ", modifiedUser=" + modifiedUser +
        ", ip=" + ip +
        ", tenantId=" + tenantId +
        "}";
    }
}
