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
 * @since 2020-10-21
 */
@Data
@TableName("BI_UI_MODEL_FIELD")
public class BiUiModelField implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    @TableField("PARENT_ID")
    private String parentId;

    @TableField("PAGE_ID")
    private String pageId;

    /**
     * 数据模型id
     */
    @TableField("MODEL_ID")
    private String modelId;

    /**
     * 所在文件夹
     */
    @TableField("FOLDER_ID")
    private String folderId;

    /**
     * 别名
     */
    @TableField("ALIAS_NAME")
    private String aliasName;

    /**
     * 物理字段名
     */
    @TableField("NAME")
    private String name;

    /**
     * 字段类型
     */
    @TableField("TYPE")
    private String type;

    /**
     * 数据类型
     */
    @TableField("DATA_TYPE")
    private String dataType;

    /**
     * 字段描述
     */
    @TableField("DESC")
    private String desc;

    /**
     * 是否隐藏
     */
    @TableField("IS_HIDDEN")
    private String isHidden;

    /**
     * 排序
     */
    @TableField("SORT_ORDER")
    private String sortOrder;

    /**
     * 是否维度
     */
    @TableField("IS_DIMENTION")
    private String isDimention;

    /**
     * 是否度量
     */
    @TableField("IS_MENSURE")
    private String isMensure;

    /**
     * 地理信息类型
     */
    @TableField("GEO_INFO_TYPE")
    private String geoInfoType;

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
