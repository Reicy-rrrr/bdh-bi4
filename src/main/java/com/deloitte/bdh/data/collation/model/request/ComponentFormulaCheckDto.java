package com.deloitte.bdh.data.collation.model.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 组件公式验证参数
 *
 * @author chenghzhang
 * @date 2020-12-17
 */
@ApiModel(description = "组件公式验证参数")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComponentFormulaCheckDto {
    @ApiModelProperty(value = "模板id", example = "10001", required = true)
    @NotNull(message = "模板id 不能为空")
    private String modelId;

    @ApiModelProperty(value = "组件id", example = "10001", required = true)
    @NotNull(message = "组件id 不能为空")
    private String componentId;

    @ApiModelProperty(value = "计算公式", example = "sales*quantity-discount", required = true)
    @NotNull(message = "计算公式")
    private String formula;
}
