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
 * @since 2021-01-27
 */
@TableName("BI_REPORT")
public class BiReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    /**
     * 批次
     */
    private String batchId;

    /**
     * 表编码
     */
    @TableField("REPORT_CODE")
    private String reportCode;

    /**
     * 表名称
     */
    @TableField("REPORT_NAME")
    private String reportName;

    /**
     * 行号
     */
    @TableField("ROW_NO")
    private String rowNo;

    /**
     * 列号
     */
    @TableField("COL_NO")
    private String colNo;

    /**
     * 指标编码
     */
    @TableField("INDEX_CODE")
    private String indexCode;

    /**
     * 指标名称
     */
    @TableField("CELL_1")
    private String cell1;

    /**
     * 指标值
     */
    @TableField("CELL_2")
    private String cell2;

    /**
     * 期间
     */
    @TableField("PERIOD")
    private String period;

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
    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }
    public String getReportCode() {
        return reportCode;
    }

    public void setReportCode(String reportCode) {
        this.reportCode = reportCode;
    }
    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }
    public String getRowNo() {
        return rowNo;
    }

    public void setRowNo(String rowNo) {
        this.rowNo = rowNo;
    }
    public String getColNo() {
        return colNo;
    }

    public void setColNo(String colNo) {
        this.colNo = colNo;
    }
    public String getIndexCode() {
        return indexCode;
    }

    public void setIndexCode(String indexCode) {
        this.indexCode = indexCode;
    }
    public String getCell1() {
        return cell1;
    }

    public void setCell1(String cell1) {
        this.cell1 = cell1;
    }
    public String getCell2() {
        return cell2;
    }

    public void setCell2(String cell2) {
        this.cell2 = cell2;
    }
    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
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
        return "BiReport{" +
        "id=" + id +
        ", batchId=" + batchId +
        ", reportCode=" + reportCode +
        ", reportName=" + reportName +
        ", rowNo=" + rowNo +
        ", colNo=" + colNo +
        ", indexCode=" + indexCode +
        ", cell1=" + cell1 +
        ", cell2=" + cell2 +
        ", period=" + period +
        ", createDate=" + createDate +
        ", createUser=" + createUser +
        ", modifiedDate=" + modifiedDate +
        ", modifiedUser=" + modifiedUser +
        ", ip=" + ip +
        ", tenantId=" + tenantId +
        "}";
    }
}
