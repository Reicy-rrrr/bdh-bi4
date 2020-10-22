package com.deloitte.bdh.data.analyse.model;

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
 * @author bo.wang
 * @since 2020-10-21
 */
@TableName("BI_UI_MODEL_FIELD")
public class BiUiModelField implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    /**
     * 数据模型id
     */
    @TableField("MODEL_ID")
    private String modelId;

    /**
     * 所在文件夹
     */
    @TableField("FOLDER_ID")
    private String folderId;

    /**
     * 别名
     */
    @TableField("ALIAS_NAME")
    private String aliasName;

    /**
     * 物理字段名
     */
    @TableField("SOURCE_FIELD")
    private String sourceField;

    /**
     * 是否隐藏
     */
    @TableField("IS_HIDDEN")
    private String isHidden;

    /**
     * 排序
     */
    @TableField("SORT_ORDER")
    private String sortOrder;

    /**
     * 数据类型
     */
    @TableField("DATA_TYPE")
    private String dataType;

    /**
     * 是否维度
     */
    @TableField("IS_DIMENTION")
    private String isDimention;

    /**
     * 是否度量
     */
    @TableField("IS_MENSURE")
    private String isMensure;

    /**
     * 地理信息类型
     */
    @TableField("GEO_INFO_TYPE")
    private String geoInfoType;

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
    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }
    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }
    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }
    public String getSourceField() {
        return sourceField;
    }

    public void setSourceField(String sourceField) {
        this.sourceField = sourceField;
    }
    public String getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(String isHidden) {
        this.isHidden = isHidden;
    }
    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    public String getIsDimention() {
        return isDimention;
    }

    public void setIsDimention(String isDimention) {
        this.isDimention = isDimention;
    }
    public String getIsMensure() {
        return isMensure;
    }

    public void setIsMensure(String isMensure) {
        this.isMensure = isMensure;
    }
    public String getGeoInfoType() {
        return geoInfoType;
    }

    public void setGeoInfoType(String geoInfoType) {
        this.geoInfoType = geoInfoType;
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
        return "BiUiModelField{" +
        "id=" + id +
        ", modelId=" + modelId +
        ", folderId=" + folderId +
        ", aliasName=" + aliasName +
        ", sourceField=" + sourceField +
        ", isHidden=" + isHidden +
        ", sortOrder=" + sortOrder +
        ", dataType=" + dataType +
        ", isDimention=" + isDimention +
        ", isMensure=" + isMensure +
        ", geoInfoType=" + geoInfoType +
        ", createDate=" + createDate +
        ", createUser=" + createUser +
        ", modifiedDate=" + modifiedDate +
        ", modifiedUser=" + modifiedUser +
        ", ip=" + ip +
        ", tenantId=" + tenantId +
        "}";
    }
}
