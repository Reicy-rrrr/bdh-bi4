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
 * @since 2020-09-27
 */
@TableName("BI_ETL_DATABASE_INF")
public class BiEtlDatabaseInf implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 数据源名称
     */
    @TableField("NAME")
    private String name;

    /**
     * 描述
     */
    @TableField("COMMENTS")
    private String comments;

    /**
     * 数据源类型（1:mysql8.*;2:msql7.*;3:oracle）
     */
    @TableField("TYPE")
    private String type;

    /**
     * 类型名称
     */
    @TableField("TYPE_NAME")
    private String typeName;

    /**
     * NIFI的连接池类型
     */
    @TableField("POOL_TYPE")
    private String poolType;

    /**
     * 数据库名称
     */
    @TableField("DB_NAME")
    private String dbName;

    /**
     * 用户名
     */
    @TableField("DB_USER")
    private String dbUser;

    /**
     * 密码
     */
    @TableField("DB_PASSWORD")
    private String dbPassword;

    /**
     * 地址
     */
    @TableField("ADDRESS")
    private String address;

    /**
     * 端口
     */
    @TableField("PORT")
    private String port;

    /**
     * 驱动
     */
    @TableField("DRIVER_NAME")
    private String driverName;

    /**
     * 服务器驱动地址
     */
    @TableField("DRIVER_LOCATIONS")
    private String driverLocations;

    /**
     * 是否有效
     */
    @TableField("EFFECT")
    private String effect;

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

    /**
     * CONTROLLER_SERVICE_ID
     */
    @TableField("CONTROLLER_SERVICE_ID")
    private String controllerServiceId;

    /**
     * ROOT_GROUP_ID
     */
    @TableField("ROOT_GROUP_ID")
    private String rootGroupId;

    /**
     * 版本
     */
    @TableField("VERSION")
    private String version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    public String getPoolType() {
        return poolType;
    }

    public void setPoolType(String poolType) {
        this.poolType = poolType;
    }
    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }
    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
    public String getDriverLocations() {
        return driverLocations;
    }

    public void setDriverLocations(String driverLocations) {
        this.driverLocations = driverLocations;
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
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "BiEtlDatabaseInf{" +
        "id=" + id +
        ", name=" + name +
        ", comments=" + comments +
        ", type=" + type +
        ", typeName=" + typeName +
        ", poolType=" + poolType +
        ", dbName=" + dbName +
        ", dbUser=" + dbUser +
        ", dbPassword=" + dbPassword +
        ", address=" + address +
        ", port=" + port +
        ", driverName=" + driverName +
        ", driverLocations=" + driverLocations +
        ", effect=" + effect +
        ", createDate=" + createDate +
        ", createUser=" + createUser +
        ", modifiedDate=" + modifiedDate +
        ", modifiedUser=" + modifiedUser +
        ", ip=" + ip +
        ", tenantId=" + tenantId +
        ", controllerServiceId=" + controllerServiceId +
        ", rootGroupId=" + rootGroupId +
        ", version=" + version +
        "}";
    }
}
