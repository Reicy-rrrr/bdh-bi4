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
 * @since 2020-11-11
 */
@TableName("BI_ETL_MODEL")
public class BiEtlModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    /**
     * 编码
     */
    @TableField("CODE")
    private String code;

    /**
     * 模型名称
     */
    @TableField("NAME")
    private String name;

    /**
     * 描述
     */
    @TableField("COMMENTS")
    private String comments;

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

    /**
     * 上级编码
     */
    @TableField("PARENT_CODE")
    private String parentCode;

    /**
     * 根节点编码
     */
    @TableField("ROOT_CODE")
    private String rootCode;

    /**
     * 是否文件夹
     */
    @TableField("IS_FILE")
    private String isFile;

    /**
     * 是否有效
     */
    @TableField("EFFECT")
    private String effect;

    /**
     * 运行状态
     */
    @TableField("STATUS")
    private String status;

    /**
     * 同步状态（0，1）
     */
    @TableField("SYNC_STATUS")
    private String syncStatus;

    /**
     * 校验状态
     */
    @TableField("VALIDATE")
    private String validate;

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
     * PROCESS_GROUP_ID
     */
    @TableField("PROCESS_GROUP_ID")
    private String processGroupId;

    /**
     * 同步时间
     */
    @TableField("CRON_EXPRESSION")
    private String cronExpression;

    /**
     * 模型内容：大json
     */
    @TableField(value = "CONTENT")
    private String content;

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
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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
    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }
    public String getRootCode() {
        return rootCode;
    }

    public void setRootCode(String rootCode) {
        this.rootCode = rootCode;
    }
    public String getIsFile() {
        return isFile;
    }

    public void setIsFile(String isFile) {
        this.isFile = isFile;
    }
    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }
    public String getValidate() {
        return validate;
    }

    public void setValidate(String validate) {
        this.validate = validate;
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
    public String getProcessGroupId() {
        return processGroupId;
    }

    public void setProcessGroupId(String processGroupId) {
        this.processGroupId = processGroupId;
    }
    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "BiEtlModel{" +
        "id=" + id +
        ", code=" + code +
        ", name=" + name +
        ", comments=" + comments +
        ", version=" + version +
        ", position=" + position +
        ", parentCode=" + parentCode +
        ", rootCode=" + rootCode +
        ", isFile=" + isFile +
        ", effect=" + effect +
        ", status=" + status +
        ", syncStatus=" + syncStatus +
        ", validate=" + validate +
        ", createDate=" + createDate +
        ", createUser=" + createUser +
        ", modifiedDate=" + modifiedDate +
        ", modifiedUser=" + modifiedUser +
        ", ip=" + ip +
        ", tenantId=" + tenantId +
        ", processGroupId=" + processGroupId +
        ", cronExpression=" + cronExpression +
        ", content=" + content +
        "}";
    }
}
