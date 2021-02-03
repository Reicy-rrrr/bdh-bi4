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
 * @since 2021-02-02
 */
@TableName("EVM_CAPANALYSIS_FUND")
public class EvmCapanalysisFund implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    /**
     * 类型
     */
    private String type;

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
     * 同比增长率
     */
    @TableField("YTY_RATE")
    private String ytyRate;

    /**
     * 环比增长率
     */
    @TableField("CHAIN_VALUE")
    private String chainValue;

    @TableField(value = "CREATE_DATE", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime createDate;

    @TableField(value = "CREATE_USER", fill = FieldFill.INSERT_UPDATE)
    private String createUser;

    @TableField(value = "MODIFIED_DATE", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime modifiedDate;

    @TableField(value = "MODIFIED_USER", fill = FieldFill.INSERT_UPDATE)
    private String modifiedUser;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
    public String getYtyRate() {
        return ytyRate;
    }

    public void setYtyRate(String ytyRate) {
        this.ytyRate = ytyRate;
    }
    public String getChainValue() {
        return chainValue;
    }

    public void setChainValue(String chainValue) {
        this.chainValue = chainValue;
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

    @Override
    public String toString() {
        return "EvmCapanalysisFund{" +
        "id=" + id +
        ", type=" + type +
        ", period=" + period +
        ", periodDate=" + periodDate +
        ", indexCode=" + indexCode +
        ", indexName=" + indexName +
        ", indexValue=" + indexValue +
        ", ytyRate=" + ytyRate +
        ", chainValue=" + chainValue +
        ", createDate=" + createDate +
        ", createUser=" + createUser +
        ", modifiedDate=" + modifiedDate +
        ", modifiedUser=" + modifiedUser +
        "}";
    }
}
