package com.deloitte.bdh.data.analyse.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author Ashen
 * @since 2020-12-15
 */
@Data
@TableName("BI_UI_ANALYSE_PAGE_COMPONENT")
public class BiUiAnalysePageComponent implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    /**
     * 组件ID
     */
    @TableField("COMPONENT_ID")
    private String componentId;

    /**
     * 父级文件夹
     */
    @TableField("PARENT_ID")
    private String parentId;

    /**
     * json配置内容
     */
    @TableField("CONTENT")
    private String content;

    /**
     * 名称
     */
    @TableField("`NAME`")
    private String name;

    /**
     * 描述
     */
    @TableField("`DESCRIBE`")
    private String describe;

    @TableField(value = "CREATE_DATE", fill = FieldFill.INSERT)
    private LocalDateTime createDate;

    @TableField(value = "CREATE_USER", fill = FieldFill.INSERT)
    private String createUser;

    @TableField(value = "MODIFIED_DATE", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime modifiedDate;

    @TableField(value = "MODIFIED_USER", fill = FieldFill.INSERT_UPDATE)
    private String modifiedUser;

    @TableField("IP")
    private String ip;

    @TableField("TENANT_ID")
    private String tenantId;

}
