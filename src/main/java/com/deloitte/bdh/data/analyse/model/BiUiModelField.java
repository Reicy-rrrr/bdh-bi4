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
    @TableField("SOURCE_FIELD")
    private String sourceField;

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
     * 数据类型
     */
    @TableField("DATA_TYPE")
    private String dataType;

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

    @TableField("CREATE_DATE")
    private LocalDateTime createDate;

    @TableField("CREATE_USER")
    private String createUser;

    @TableField("MODIFIED_DATE")
    private LocalDateTime modifiedDate;

    @TableField("MODIFIED_USER")
    private String modifiedUser;

    @TableField("IP")
    private String ip;

    @TableField("TENANT_ID")
    private String tenantId;
}
