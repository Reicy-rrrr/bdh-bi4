package com.deloitte.bdh.data.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author Ashen
 * @since 2020-08-20
 */
@TableName("FND_PORTAL_USER")
public class FndPortalUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableId("USER_ID")
    private String userId;

    /**
     * 用户全名
     */
    @TableField("FULL_NAME")
    private String fullName;

    /**
     * 密码
     */
    @TableField("PASSWORD")
    private String password;

    /**
     * 移动电话
     */
    @TableField("MOBILE_PHONE")
    private String mobilePhone;

    /**
     * 邮箱
     */
    @TableField("EMAIL")
    private String email;

    /**
     * 头像
     */
    @TableField("IMAGE")
    private String image;

    /**
     * 状态(0:锁定,1:待审核,2:正常)
     */
    @TableField("USER_STATUS_ENUM_VAL")
    private String userStatusEnumVal;

    /**
     * 活动标识
     */
    @TableField("ACTIVE_FLAG")
    private BigDecimal activeFlag;

    /**
     * 生效日期
     */
    @TableField("START_DATE_ACTIVE")
    private LocalDateTime startDateActive;

    /**
     * 失效日期
     */
    @TableField("END_DATE_ACTIVE")
    private LocalDateTime endDateActive;

    /**
     * 创建者
     */
    @TableField("CREATE_USER")
    private String createUser;

    /**
     * 创建时间
     */
    @TableField("CREATE_DATE")
    private LocalDateTime createDate;

    /**
     * 修改者
     */
    @TableField("MODIFIED_USER")
    private String modifiedUser;

    /**
     * 修改日期
     */
    @TableField("MODIFIED_DATE")
    private LocalDateTime modifiedDate;

    /**
     * 操作ip
     */
    @TableField("IP")
    private String ip;

    /**
     * 租户id
     */
    @TableField("TENANT_ID")
    private String tenantId;

    /**
     * 手机号国家代码
     */
    @TableField("PHONE_CODE")
    private String phoneCode;

    /**
     * 员工ID
     */
    @TableField("EMPLOYEE_ID")
    private String employeeId;

    /**
     * 密码修改更新标识
     */
    @TableField("PASSWORD_FLAG")
    private String passwordFlag;

    /**
     * 手机号国家代码id
     */
    @TableField("PHONE_CODE_ID")
    private String phoneCodeId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    public String getUserStatusEnumVal() {
        return userStatusEnumVal;
    }

    public void setUserStatusEnumVal(String userStatusEnumVal) {
        this.userStatusEnumVal = userStatusEnumVal;
    }
    public BigDecimal getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(BigDecimal activeFlag) {
        this.activeFlag = activeFlag;
    }
    public LocalDateTime getStartDateActive() {
        return startDateActive;
    }

    public void setStartDateActive(LocalDateTime startDateActive) {
        this.startDateActive = startDateActive;
    }
    public LocalDateTime getEndDateActive() {
        return endDateActive;
    }

    public void setEndDateActive(LocalDateTime endDateActive) {
        this.endDateActive = endDateActive;
    }
    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }
    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }
    public String getModifiedUser() {
        return modifiedUser;
    }

    public void setModifiedUser(String modifiedUser) {
        this.modifiedUser = modifiedUser;
    }
    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
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
    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }
    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
    public String getPasswordFlag() {
        return passwordFlag;
    }

    public void setPasswordFlag(String passwordFlag) {
        this.passwordFlag = passwordFlag;
    }
    public String getPhoneCodeId() {
        return phoneCodeId;
    }

    public void setPhoneCodeId(String phoneCodeId) {
        this.phoneCodeId = phoneCodeId;
    }

    @Override
    public String toString() {
        return "FndPortalUser{" +
        "userId=" + userId +
        ", fullName=" + fullName +
        ", password=" + password +
        ", mobilePhone=" + mobilePhone +
        ", email=" + email +
        ", image=" + image +
        ", userStatusEnumVal=" + userStatusEnumVal +
        ", activeFlag=" + activeFlag +
        ", startDateActive=" + startDateActive +
        ", endDateActive=" + endDateActive +
        ", createUser=" + createUser +
        ", createDate=" + createDate +
        ", modifiedUser=" + modifiedUser +
        ", modifiedDate=" + modifiedDate +
        ", ip=" + ip +
        ", tenantId=" + tenantId +
        ", phoneCode=" + phoneCode +
        ", employeeId=" + employeeId +
        ", passwordFlag=" + passwordFlag +
        ", phoneCodeId=" + phoneCodeId +
        "}";
    }
}
