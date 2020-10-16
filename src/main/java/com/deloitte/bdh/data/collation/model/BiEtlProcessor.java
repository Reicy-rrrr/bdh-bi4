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
 * @since 2020-10-01
 */
@TableName("BI_ETL_PROCESSOR")
public class BiEtlProcessor implements Serializable {

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
     * 类型
     */
    @TableField("TYPE")
    private String type;

    /**
     * 类型描述
     */
    @TableField("TYPE_DESC")
    private String typeDesc;

    /**
     * 坐标
     */
    @TableField("POSITION")
    private String position;

    /**
     * 运行状态
     */
    @TableField("STATUS")
    private String status;

    /**
     * 是否有效
     */
    @TableField("EFFECT")
    private String effect;

    /**
     * 校验状态
     */
    @TableField("VALIDATE")
    private String validate;

    /**
     * 校验结果
     */
    @TableField("VALIDATE_MESSAGE")
    private String validateMessage;

    /**
     * PROCESS_ID
     */
    @TableField("PROCESS_ID")
    private String processId;

    /**
     * PROCESS_GROUP_ID
     */
    @TableField("PROCESS_GROUP_ID")
    private String processGroupId;

    /**
     * 关联的processors 编码
     */
    @TableField("REL_PROCESSORS_CODE")
    private String relProcessorsCode;

    /**
     * 版本号
     */
    @TableField("VERSION")
    private String version;

    /**
     * processor 关系表
     */
    @TableField("RELATIONSHIPS")
    private String relationships;

    /**
     * 序号
     */
    @TableField("SEQUENCE")
    private String sequence;

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
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getTypeDesc() {
        return typeDesc;
    }

    public void setTypeDesc(String typeDesc) {
        this.typeDesc = typeDesc;
    }
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }
    public String getValidate() {
        return validate;
    }

    public void setValidate(String validate) {
        this.validate = validate;
    }
    public String getValidateMessage() {
        return validateMessage;
    }

    public void setValidateMessage(String validateMessage) {
        this.validateMessage = validateMessage;
    }
    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }
    public String getProcessGroupId() {
        return processGroupId;
    }

    public void setProcessGroupId(String processGroupId) {
        this.processGroupId = processGroupId;
    }
    public String getRelProcessorsCode() {
        return relProcessorsCode;
    }

    public void setRelProcessorsCode(String relProcessorsCode) {
        this.relProcessorsCode = relProcessorsCode;
    }
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    public String getRelationships() {
        return relationships;
    }

    public void setRelationships(String relationships) {
        this.relationships = relationships;
    }
    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
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
        return "BiEtlProcessor{" +
        "id=" + id +
        ", code=" + code +
        ", name=" + name +
        ", type=" + type +
        ", typeDesc=" + typeDesc +
        ", position=" + position +
        ", status=" + status +
        ", effect=" + effect +
        ", validate=" + validate +
        ", validateMessage=" + validateMessage +
        ", processId=" + processId +
        ", processGroupId=" + processGroupId +
        ", relProcessorsCode=" + relProcessorsCode +
        ", version=" + version +
        ", relationships=" + relationships +
        ", sequence=" + sequence +
        ", createDate=" + createDate +
        ", createUser=" + createUser +
        ", modifiedDate=" + modifiedDate +
        ", modifiedUser=" + modifiedUser +
        ", ip=" + ip +
        ", tenantId=" + tenantId +
        "}";
    }
}
