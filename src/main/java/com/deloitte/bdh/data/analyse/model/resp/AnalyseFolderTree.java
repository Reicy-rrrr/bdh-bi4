package com.deloitte.bdh.data.analyse.model.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class AnalyseFolderTree implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID", example = "")
    private String id;

    /**
     * 数据模型id
     */
    @ApiModelProperty(value = "数据模型id", example = "")
    private String modelId;

    /**
     * 上级id
     */
    @ApiModelProperty(value = "上级id", example = "")
    private String parentId;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称", example = "")
    private String name;

    /**
     * 类型
     */
    @ApiModelProperty(value = "类型", example = "")
    private String type;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序", example = "")
    private Integer sortOrder;

    /**
     * 字段
     */
    @ApiModelProperty(value = "字段", example = "")
    private List<AnalyseFieldTree> children;

}
