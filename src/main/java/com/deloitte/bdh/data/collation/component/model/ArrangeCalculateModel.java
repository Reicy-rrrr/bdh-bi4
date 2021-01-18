package com.deloitte.bdh.data.collation.component.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 整理组件计算模型
 *
 * @author chenghzhang
 * @date 2020/12/14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArrangeCalculateModel {
    /** 新字段名称 */
    @ApiModelProperty(value = "新字段名称", example = "利润汇总", required = true)
    private String name;
    @ApiModelProperty(value = "计算公式", example = "sales*quantity-discount", required = true)
    @NotNull(message = "计算公式")
    private String formula;
}
