package com.deloitte.bdh.data.collation.component.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 整理组件：大小写转换模型
 *
 * @author chenghzhang
 * @date 2020/11/09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArrangeCaseConvertModel {
    /** 字段名称 */
    @ApiModelProperty(value = "字段名称", example = "user_name", required = true)
    private String name;
    /** 拆分类型 */
    @ApiModelProperty(value = "拆分类型", example = "upper/lower", required = true)
    private String type;
}
