package com.deloitte.bdh.data.analyse.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Author:LIJUN
 * Date:08/12/2020
 * Description:
 */
@Data
@TableName("BI_UI_ANALYSE_USER_RESOURCE")
public class BiUiAnalyseUserResource implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "USER_RESOURCE_ID", type = IdType.AUTO)
    private String userResourceId;

    /**
     * 用户id
     */
    @TableField("USER_ID")
    private String userId;

    /**
     * 资源id
     */
    @TableField("RESOURCE_ID")
    private String resourceId;

    /**
     * 资源类型：'page'，'category'
     */
    @TableField("RESOURCE_TYPE")
    private String resourceType;

    /**
     * 权限'view','edit'
     */
    @TableField("PERMITTED_ACTION")
    private String permittedAction;

    @TableField("TENANT_ID")
    private String tenantId;

    @TableField("CREATE_DATE")
    private LocalDateTime createDate;

    @TableField("CREATE_USER")
    private String createUser;

    @TableField("MODIFIED_DATE")
    private LocalDateTime modifiedDate;

    @TableField("MODIFIED_USER")
    private String modifiedUser;

}
