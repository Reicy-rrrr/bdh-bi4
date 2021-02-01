package com.deloitte.bdh.data.collation.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
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
 * @since 2021-01-28
 */
@TableName("BI_REPORT_OUT")
public class BiReportOut implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    /**
     * 期间
     */
    @TableField("PERIOD")
    private String period;

    /**
     * 期间日期
     */
    @TableField("PERIOD_DATE")
    private String periodDate;

    /**
     * 表名称
     */
    @TableField("REPORT_NAME")
    private String reportName;

    /**
     * 指标编码
     */
    @TableField("INDEX_CODE")
    private String indexCode;

    /**
     * 指标名称
     */
    @TableField("INDEX_NAME")
    private String indexName;

    /**
     * 指标值
     */
    @TableField("INDEX_VALUE")
    private String indexValue;

    /**
     * 同比值
     */
    @TableField("YTY_VALUE")
    private String ytyValue;

    /**
     * 同比增长率
     */
    @TableField("YTY_RATE")
    private String ytyRate;

    @TableField(value = "CREATE_DATE", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime createDate;

    @TableField(value = "CREATE_USER", fill = FieldFill.INSERT_UPDATE)
    private String createUser;

    @TableField(value = "MODIFIED_DATE", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime modifiedDate;

    @TableField(value = "MODIFIED_USER", fill = FieldFill.INSERT_UPDATE)
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
    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }
    public String getPeriodDate() {
        return periodDate;
    }

    public void setPeriodDate(String periodDate) {
        this.periodDate = periodDate;
    }
    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }
    public String getIndexCode() {
        return indexCode;
    }

    public void setIndexCode(String indexCode) {
        this.indexCode = indexCode;
    }
    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }
    public String getIndexValue() {
        return indexValue;
    }

    public void setIndexValue(String indexValue) {
        this.indexValue = indexValue;
    }
    public String getYtyValue() {
        return ytyValue;
    }

    public void setYtyValue(String ytyValue) {
        this.ytyValue = ytyValue;
    }
    public String getYtyRate() {
        return ytyRate;
    }

    public void setYtyRate(String ytyRate) {
        this.ytyRate = ytyRate;
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
        return "BiReportOut{" +
        "id=" + id +
        ", period=" + period +
        ", periodDate=" + periodDate +
        ", reportName=" + reportName +
        ", indexCode=" + indexCode +
        ", indexName=" + indexName +
        ", indexValue=" + indexValue +
        ", ytyValue=" + ytyValue +
        ", ytyRate=" + ytyRate +
        ", createDate=" + createDate +
        ", createUser=" + createUser +
        ", modifiedDate=" + modifiedDate +
        ", modifiedUser=" + modifiedUser +
        ", ip=" + ip +
        ", tenantId=" + tenantId +
        "}";
    }
}
