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
 * @since 2020-10-28
 */
@TableName("BI_ETL_SYNC_PLAN")
public class BiEtlSyncPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 计划编码
     */
    @TableField("CODE")
    private String code;

    /**
     * 计划组编码
     */
    @TableField("GROUP_CODE")
    private String groupCode;

    /**
     * 执行计划类型（数据同步、数据整理）
     */
    @TableField("PLAN_TYPE")
    private String planType;

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
     * 是否第一次整理时的同步操作
     */
    @TableField("IS_FIRST")
    private String isFirst;

    /**
     * 执行状态（待执行、执行中、执行完成）
     */
    @TableField("PLAN_STATUS")
    private String planStatus;

    /**
     * 执行结果（失败、成功）
     */
    @TableField("PLAN_RESULT")
    private String planResult;

    /**
     * 处理次数
     */
    @TableField("PROCESS_COUNT")
    private String processCount;

    /**
     * 执行SQL
     */
    @TableField("PLAN_SQL")
    private String planSql;

    /**
     * 执行SQL总条数
     */
    @TableField("SQL_COUNT")
    private String sqlCount;

    /**
     * 执行SQL本地总条数
     */
    @TableField("SQL_LOCAL_COUNT")
    private String sqlLocalCount;

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
    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }
    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
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
    public String getIsFirst() {
        return isFirst;
    }

    public void setIsFirst(String isFirst) {
        this.isFirst = isFirst;
    }
    public String getPlanStatus() {
        return planStatus;
    }

    public void setPlanStatus(String planStatus) {
        this.planStatus = planStatus;
    }
    public String getPlanResult() {
        return planResult;
    }

    public void setPlanResult(String planResult) {
        this.planResult = planResult;
    }
    public String getProcessCount() {
        return processCount;
    }

    public void setProcessCount(String processCount) {
        this.processCount = processCount;
    }
    public String getPlanSql() {
        return planSql;
    }

    public void setPlanSql(String planSql) {
        this.planSql = planSql;
    }
    public String getSqlCount() {
        return sqlCount;
    }

    public void setSqlCount(String sqlCount) {
        this.sqlCount = sqlCount;
    }
    public String getSqlLocalCount() {
        return sqlLocalCount;
    }

    public void setSqlLocalCount(String sqlLocalCount) {
        this.sqlLocalCount = sqlLocalCount;
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
        return "BiEtlSyncPlan{" +
        "id=" + id +
        ", code=" + code +
        ", groupCode=" + groupCode +
        ", planType=" + planType +
        ", refModelCode=" + refModelCode +
        ", refMappingCode=" + refMappingCode +
        ", isFirst=" + isFirst +
        ", planStatus=" + planStatus +
        ", planResult=" + planResult +
        ", processCount=" + processCount +
        ", planSql=" + planSql +
        ", sqlCount=" + sqlCount +
        ", sqlLocalCount=" + sqlLocalCount +
        ", createDate=" + createDate +
        ", createUser=" + createUser +
        ", modifiedDate=" + modifiedDate +
        ", modifiedUser=" + modifiedUser +
        ", ip=" + ip +
        ", tenantId=" + tenantId +
        "}";
    }
}
