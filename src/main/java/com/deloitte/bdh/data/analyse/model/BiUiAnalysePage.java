package com.deloitte.bdh.data.analyse.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author bo.wang
 * @since 2020-10-19
 */
@Data
@TableName("BI_UI_ANALYSE_PAGE")
public class BiUiAnalysePage implements Serializable {

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
     *
     */
    @TableField("TYPE")
    private String type;

    /**
     * 上级id
     */
    @TableField("PARENT_ID")
    private String parentId;

    /**
     * 当前编辑的id
     */
    @TableField("EDIT_ID")
    private String editId;

    /**
     * 发布后写入的id,重复发布都修改这个值为当前版本
     */
    @TableField("PUBLISH_ID")
    private String publishId;

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

    @TableField("IS_EDIT")
    private String isEdit;

    @TableField("IP")
    private String ip;

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
