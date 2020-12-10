package com.deloitte.bdh.data.analyse.model.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AnalyseCategoryTree implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    private String id;

    /**
     * 文件夹编码
     */
    @ApiModelProperty(value = "CODE")
    private String code;

    /**
     * 报表名称
     */
    @ApiModelProperty(value = "NAME")
    private String name;

    /**
     * predefined,customer 我的分析,预定义报表
     */
    @ApiModelProperty(value = "TYPE")
    private String type;

    /**
     * 系统初始化,自建
     */
    @ApiModelProperty(value = "下级数据类型")
    private String childrenType;

    /**
     * 上级id
     */
    @ApiModelProperty(value = "PARENT_ID")
    private String parentId;

    /**
     * 文件夹
     */
    @ApiModelProperty(value = "DES")
    private String des;

    /**
     * 报表描述
     */
    @ApiModelProperty(value = "ICON")
    private String icon;

    @ApiModelProperty(value = "permitted Action")
    private String permittedAction;

    /**
     * 下级数据
     */
    @ApiModelProperty(value = "下级数据")
    List<AnalyseCategoryTree> children;

}
