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
@TableName("BI_COMPONENT_CONNECTION")
public class BiComponentConnection implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    /**
     * 编码
     */
    @TableField("CODE")
    private String code;

    /**
     * 关联COMPONENT编码
     */
    @TableField("FROM_COMPONENT_CODE")
    private String fromComponentCode;

    /**
     * 被关联COMPONENT编码
     */
    @TableField("TO_COMPONENT_CODE")
    private String toComponentCode;

    /**
     * 所属模板code
     */
    @TableField("REL_MODEL_CODE")
    private String relModelCode;

    /**
     * 版本号
     */
    @TableField("VERSION")
    private String version;

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
    public String getFromComponentCode() {
        return fromComponentCode;
    }

    public void setFromComponentCode(String fromComponentCode) {
        this.fromComponentCode = fromComponentCode;
    }
    public String getToComponentCode() {
        return toComponentCode;
    }

    public void setToComponentCode(String toComponentCode) {
        this.toComponentCode = toComponentCode;
    }
    public String getRelModelCode() {
        return relModelCode;
    }

    public void setRelModelCode(String relModelCode) {
        this.relModelCode = relModelCode;
    }
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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
        return "BiComponentConnection{" +
        "id=" + id +
        ", code=" + code +
        ", fromComponentCode=" + fromComponentCode +
        ", toComponentCode=" + toComponentCode +
        ", relModelCode=" + relModelCode +
        ", version=" + version +
        ", createDate=" + createDate +
        ", createUser=" + createUser +
        ", modifiedDate=" + modifiedDate +
        ", modifiedUser=" + modifiedUser +
        ", ip=" + ip +
        ", tenantId=" + tenantId +
        "}";
    }
}
