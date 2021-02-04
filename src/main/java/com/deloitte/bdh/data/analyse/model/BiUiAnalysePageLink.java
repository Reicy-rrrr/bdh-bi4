package com.deloitte.bdh.data.analyse.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("BI_UI_ANALYSE_PAGE_LINK")
public class BiUiAnalysePageLink implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    /**
     * 报表ID
     */
    @TableField("PAGE_ID")
    private String pageId;

    /**
     * 报表ID
     */
    @TableField("REF_PAGE_ID")
    private String refPageId;

    @TableField("IP")
    private String ip;

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
