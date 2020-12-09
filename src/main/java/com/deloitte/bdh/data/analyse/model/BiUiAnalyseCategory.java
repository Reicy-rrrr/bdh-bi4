package com.deloitte.bdh.data.analyse.model;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author bo.wang
 * @since 2020-10-19
 */
@Data
@TableName("BI_UI_ANALYSE_CATEGORY")
public class BiUiAnalyseCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    /**
     * 报表编码
     */
    @TableField("CODE")
    private String code;

    /**
     * 报表名称
     */
    @TableField("NAME")
    private String name;

    /**
     * predefined,customer 我的分析,预定义报表
     */
    @TableField("TYPE")
    private String type;

    /**
     * 上级id
     */
    @TableField("PARENT_ID")
    private String parentId;

    /**
     * 报表描述
     */
    @TableField("DES")
    private String des;

    /**
     * 报表描述
     */
    @TableField("ICON")
    private String icon;

    @TableField("IP")
    private String ip;

    @TableField("TENANT_ID")
    private String tenantId;

    @TableField(value = "CREATE_DATE", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime createDate;

    @TableField(value = "CREATE_USER", fill = FieldFill.INSERT_UPDATE)
    private String createUser;

    @TableField(value = "MODIFIED_DATE", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime modifiedDate;

    @TableField(value = "MODIFIED_USER", fill = FieldFill.INSERT_UPDATE)
    private String modifiedUser;

}
