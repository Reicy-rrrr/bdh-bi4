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
    @TableField("VALUE")
    private String value;

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
