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
 * @since 2021-02-03
 */
@TableName("BI_EVM_FILE")
public class BiEvmFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 批次id
     */
    @TableField("BATCH_ID")
    private String batchId;

    /**
     * 原始文件名
     */
    @TableField("ORIGINAL_FILE_NAME")
    private String originalFileName;

    /**
     * 存储文件名
     */
    @TableField("STORED_FILE_NAME")
    private String storedFileName;

    /**
     * 存储文件的key（存储OSS时返回的key）
     */
    @TableField("STORED_FILE_KEY")
    private String storedFileKey;

    /**
     * 文件类型
     */
    @TableField("FILE_TYPE")
    private String fileType;

    /**
     * 文件存储目录
     */
    @TableField("FILE_PATH")
    private String filePath;

    /**
     * 文件大小：默认以B为单位
     */
    @TableField("FILE_SIZE")
    private String fileSize;

    /**
     * 数据刷新的表名
     */
    @TableField("TABLES")
    private String tables;

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
    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }
    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }
    public String getStoredFileName() {
        return storedFileName;
    }

    public void setStoredFileName(String storedFileName) {
        this.storedFileName = storedFileName;
    }
    public String getStoredFileKey() {
        return storedFileKey;
    }

    public void setStoredFileKey(String storedFileKey) {
        this.storedFileKey = storedFileKey;
    }
    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }
    public String getTables() {
        return tables;
    }

    public void setTables(String tables) {
        this.tables = tables;
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
        return "BiEvmFile{" +
        "id=" + id +
        ", batchId=" + batchId +
        ", originalFileName=" + originalFileName +
        ", storedFileName=" + storedFileName +
        ", storedFileKey=" + storedFileKey +
        ", fileType=" + fileType +
        ", filePath=" + filePath +
        ", fileSize=" + fileSize +
        ", tables=" + tables +
        ", createDate=" + createDate +
        ", createUser=" + createUser +
        ", modifiedDate=" + modifiedDate +
        ", modifiedUser=" + modifiedUser +
        ", ip=" + ip +
        ", tenantId=" + tenantId +
        "}";
    }
}
