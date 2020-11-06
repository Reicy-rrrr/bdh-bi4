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
 * @since 2020-11-03
 */
@TableName("BI_COMPONENT")
public class BiComponent implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    /**
     * 编码
     */
    @TableField("CODE")
    private String code;

    /**
     * 处理器名称
     */
    @TableField("NAME")
    private String name;

    /**
     * 组件类型
     */
    @TableField("TYPE")
    private String type;

    /**
     * 是否有效
     */
    @TableField("EFFECT")
    private String effect;

    /**
     * 所属模板code
     */
    @TableField("REF_MODEL_CODE")
    private String refModelCode;

    /**
     * 关联映射code（数据源组件）
     */
    @TableField("REF_MAPPING_CODE")
    private String refMappingCode;

    /**
     * 版本号
     */
    @TableField("VERSION")
    private String version;

    /**
     * 坐标
     */
    @TableField("POSITION")
    private String position;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public String getRefModelCode() {
        return refModelCode;
    }

    public void setRefModelCode(String refModelCode) {
        this.refModelCode = refModelCode;
    }

    public String getRefMappingCode() {
        return refMappingCode;
    }

    public void setRefMappingCode(String refMappingCode) {
        this.refMappingCode = refMappingCode;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
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
        return "BiComponent{" +
                "id=" + id +
                ", code=" + code +
                ", name=" + name +
                ", type=" + type +
                ", effect=" + effect +
                ", refModelCode=" + refModelCode +
                ", refMappingCode=" + refMappingCode +
                ", version=" + version +
                ", position=" + position +
                ", createDate=" + createDate +
                ", createUser=" + createUser +
                ", modifiedDate=" + modifiedDate +
                ", modifiedUser=" + modifiedUser +
                ", ip=" + ip +
                ", tenantId=" + tenantId +
                "}";
    }
}
