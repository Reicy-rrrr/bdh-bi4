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
 * @since 2020-12-08
 */
@TableName("BI_TENANT_CONFIG")
public class BiTenantConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    /**
     * 本地数据源类型（1:mysq,2:hive）
     */
    @TableField("TYPE")
    private String type;

    /**
     * 本地数据源
     */
    @TableField("CONTROLLER_SERVICE_ID")
    private String controllerServiceId;

    /**
     * ROOT_GROUP_ID
     */
    @TableField("ROOT_GROUP_ID")
    private String rootGroupId;

    /**
     * 是否可用
     */
    @TableField("EFFECT")
    private String effect;

    @TableField("CREATE_DATE")
    private LocalDateTime createDate;

    @TableField("CREATE_USER")
    private String createUser;

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
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getControllerServiceId() {
        return controllerServiceId;
    }

    public void setControllerServiceId(String controllerServiceId) {
        this.controllerServiceId = controllerServiceId;
    }
    public String getRootGroupId() {
        return rootGroupId;
    }

    public void setRootGroupId(String rootGroupId) {
        this.rootGroupId = rootGroupId;
    }
    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
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
        return "BiTenantConfig{" +
        "id=" + id +
        ", type=" + type +
        ", controllerServiceId=" + controllerServiceId +
        ", rootGroupId=" + rootGroupId +
        ", effect=" + effect +
        ", createDate=" + createDate +
        ", createUser=" + createUser +
        ", ip=" + ip +
        ", tenantId=" + tenantId +
        "}";
    }
}
