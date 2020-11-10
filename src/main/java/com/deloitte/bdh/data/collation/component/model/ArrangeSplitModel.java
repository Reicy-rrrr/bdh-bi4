package com.deloitte.bdh.data.collation.component.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 整理组件长度拆分模型
 *
 * @author chenghzhang
 * @date 2020/11/09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArrangeSplitModel {
    /** 字段名称 */
    @ApiModelProperty(value = "字段名称", example = "user_name", required = true)
    private String name;
    /** 拆分类型 */
    @ApiModelProperty(value = "拆分类型", example = "length/separator", required = true)
    private String type;
    /** 拆分值（分隔符或者长度） */
    @ApiModelProperty(value = "拆分值（分隔符或者长度）", example = ",/10", required = true)
    private String value;
}
