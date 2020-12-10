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
 * @since 2020-12-10
 */
@TableName("BI_DATA_SET")
public class BiDataSet implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    /**
     * 数据集合类型（0, "数据直连"，1, "数据整理"）
     */
    @TableField("TYPE")
    private String type;

    /**
     * 同步的数据源id
     */
    @TableField("REF_SOURCE_ID")
    private String refSourceId;

    /**
     * 表名称
     */
    @TableField("TABLE_NAME")
    private String tableName;

    /**
     * 表描述
     */
    @TableField("TABLE_DESC")
    private String tableDesc;

    /**
     * 所属模板code
     */
    @TableField("REF_MODEL_CODE")
    private String refModelCode;

    /**
     * 所属文件夹ID
     */
    @TableField("PARENT_ID")
    private String parentId;

    /**
     * 是否文件夹
     */
    @TableField("IS_FILE")
    private String isFile;

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
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getRefSourceId() {
        return refSourceId;
    }

    public void setRefSourceId(String refSourceId) {
        this.refSourceId = refSourceId;
    }
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    public String getTableDesc() {
        return tableDesc;
    }

    public void setTableDesc(String tableDesc) {
        this.tableDesc = tableDesc;
    }
    public String getRefModelCode() {
        return refModelCode;
    }

    public void setRefModelCode(String refModelCode) {
        this.refModelCode = refModelCode;
    }
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
    public String getIsFile() {
        return isFile;
    }

    public void setIsFile(String isFile) {
        this.isFile = isFile;
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
        return "BiDataSet{" +
        "id=" + id +
        ", type=" + type +
        ", refSourceId=" + refSourceId +
        ", tableName=" + tableName +
        ", tableDesc=" + tableDesc +
        ", refModelCode=" + refModelCode +
        ", parentId=" + parentId +
        ", isFile=" + isFile +
        ", createDate=" + createDate +
        ", createUser=" + createUser +
        ", modifiedDate=" + modifiedDate +
        ", modifiedUser=" + modifiedUser +
        ", ip=" + ip +
        ", tenantId=" + tenantId +
        "}";
    }
}
