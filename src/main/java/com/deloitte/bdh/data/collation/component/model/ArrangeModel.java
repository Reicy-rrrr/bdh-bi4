package com.deloitte.bdh.data.collation.component.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 整理组件模型
 *
 * @author chenghzhang
 * @date 2020/11/09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArrangeModel {
    /** 分组整理模型 */
    @ApiModelProperty(value = "分组整理模型", example = "", required = true)
    private ArrangeSplitModel lengthSplit;
    /** 分组整理模型 */
    @ApiModelProperty(value = "分组整理模型", example = "", required = true)
    private ArrangeReplaceModel replace;
    /** 分组整理模型 */
    @ApiModelProperty(value = "分组整理模型", example = "", required = true)
    private ArrangeNonNullModel nonNull;
    /** 分组整理模型 */
    @ApiModelProperty(value = "分组整理模型", example = "", required = true)
    private ArrangeUpperModel uppercase;
    /** 分组整理模型 */
    @ApiModelProperty(value = "分组整理模型", example = "", required = true)
    private ArrangeLowerModel lowercase;
    /** 分组整理模型 */
    @ApiModelProperty(value = "分组整理模型", example = "", required = true)
    private ArrangeTrimModel trim;
    /** 分组整理模型 */
    @ApiModelProperty(value = "分组整理模型", example = "", required = true)
    private ArrangeGroupModel group;
}
