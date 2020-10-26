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
@TableName("BI_ETL_SYNC_LOG")
public class BiEtlSyncLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 计划编码
     */
    @TableField("PLAN_CODE")
    private String planCode;

    /**
     * 所属模板code
     */
    @TableField("REF_MODEL_CODE")
    private String refModelCode;

    /**
     * 映射编码
     */
    @TableField("REF_MAPPING_CODE")
    private String refMappingCode;

    /**
     * 执行状态
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
    public String getPlanCode() {
        return planCode;
    }

    public void setPlanCode(String planCode) {
        this.planCode = planCode;
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
        return "BiEtlSyncLog{" +
        "id=" + id +
        ", planCode=" + planCode +
        ", refModelCode=" + refModelCode +
        ", refMappingCode=" + refMappingCode +
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
