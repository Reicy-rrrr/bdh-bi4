package com.deloitte.bdh.data.analyse.model.resp;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
public class AnalyseFieldTree {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private String id;

    /**
     * 上级ID
     */
    @ApiModelProperty(value = "上级ID")
    private String parentId;

    /**
     * 字段类型
     */
    @ApiModelProperty(value = "字段类型")
    private String type;

    /**
     * 字段名称
     */
    @ApiModelProperty(value = "字段名称", example = "name")
    private String name;

    /**
     * 字段描述
     */
    @ApiModelProperty(value = "字段描述", example = "名称")
    private String desc;

    /**
     * 字段数据类型
     */
    @ApiModelProperty(value = "字段数据类型", example = "decimal")
    private String dataType;

    /**
     * 数据模型类型
     */
    @ApiModelProperty(value = "TOP,FOLDER,FIELD")
    private String modelType;
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
    /**
     * 树状展开需要字段
     */
    @ApiModelProperty(value = "下级数据")
    List<AnalyseFieldTree> children;
}
