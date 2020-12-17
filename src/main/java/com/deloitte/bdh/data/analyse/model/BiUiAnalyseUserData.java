package com.deloitte.bdh.data.analyse.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Author:LIJUN
 * Date:08/12/2020
 * Description:
 */
@Data
@TableName("BI_UI_ANALYSE_USER_DATA")
public class BiUiAnalyseUserData implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "USER_DATA_ID", type = IdType.AUTO)
    private String userDataId;

    /**
     * 用户id
     */
    @TableField("USER_ID")
    private String userId;

    /**
     * 报表id
     */
    @TableField("PAGE_ID")
    private String pageId;

    /**
     * 组件id
     */
    @TableField("COMPONENT_ID")
    private String componentId;

    /**
     * 表
     */
    @TableField("TABLE_NAME")
    private String tableName;

    /**
     * 字段
     */
    @TableField("TABLE_FIELD")
    private String tableField;

    /**
     * 值
     */
    @TableField("FIELD_VALUE")
    private String fieldValue;

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

}
