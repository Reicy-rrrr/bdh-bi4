package com.deloitte.bdh.data.analyse.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author bo.wang
 * @since 2020-10-22
 */
@Data
@TableName("BI_UI_ANALYSE_DEFAULT_CATEGORY")
public class BiUiAnalyseDefaultCategory implements Serializable {

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
     * 报表描述
     */
    @TableField("DES")
    private String des;

    /**
     * 上级id
     */
    @TableField("PARENT_ID")
    private String parentId;

    /**
     * predefined,customer
     */
    @TableField("TYPE")
    private String type;
    /**
     * 图标
     */
    @TableField("ICON")
    private String icon;

    @TableField("CREATE_DATE")
    private LocalDateTime createDate;

    @TableField("CREATE_USER")
    private String createUser;

    @TableField("MODIFIED_DATE")
    private LocalDateTime modifiedDate;

    @TableField("MODIFIED_USER")
    private String modifiedUser;
}
