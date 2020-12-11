package com.deloitte.bdh.data.analyse.model;

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
 * @since 2020-11-24
 */
@TableName("BI_UI_ANALYSE_PUBLIC_SHARE")
public class BiUiAnalysePublicShare implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    /**
     * 关联报表ID
     */
    @TableField("REF_PAGE_ID")
    private String refPageId;

    /**
     * 公开类型（0：不公开，1：公开，2：加密公开）
     */
    @TableField("TYPE")
    private String type;

    /**
     * 密码
     */
    @TableField("PASSWORD")
    private String password;

    /**
     * 公开分享的地址
     */
    @TableField("ADDRESS")
    private String address;

    /**
     * 加密的编码
     */
    @TableField("CODE")
    private String code;

    @TableField("TENANT_ID")
    private String tenantId;

    @TableField(value = "CREATE_DATE", fill = FieldFill.INSERT)
    private LocalDateTime createDate;

    @TableField(value = "CREATE_USER", fill = FieldFill.INSERT)
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
    public String getRefPageId() {
        return refPageId;
    }

    public void setRefPageId(String refPageId) {
        this.refPageId = refPageId;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
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
        return "BiUiAnalysePublicShare{" +
        "id=" + id +
        ", refPageId=" + refPageId +
        ", type=" + type +
        ", password=" + password +
        ", address=" + address +
        ", code=" + code +
        ", tenantId=" + tenantId +
        ", createDate=" + createDate +
        ", createUser=" + createUser +
        ", modifiedDate=" + modifiedDate +
        ", modifiedUser=" + modifiedUser +
        "}";
    }
}
