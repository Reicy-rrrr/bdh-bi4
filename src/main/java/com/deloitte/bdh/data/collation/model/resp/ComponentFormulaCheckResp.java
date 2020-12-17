package com.deloitte.bdh.data.collation.model.resp;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 组件公式验证结果
 *
 * @author chenghzhang
 * @date 2020-12-17
 */
@ApiModel(description = "组件公式验证参数")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComponentFormulaCheckResp {
    @ApiModelProperty(value = "验证标识", example = "true/false", required = true)
    private Boolean flag;

    @ApiModelProperty(value = "验证信息", example = "10001", required = true)
    private String message;
}
