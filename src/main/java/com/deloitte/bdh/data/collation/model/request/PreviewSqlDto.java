package com.deloitte.bdh.data.collation.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 预览SQL
 *
 * @author chenghzhang
 * @date 2020/10/26
 */
@ApiModel(description = "预览")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PreviewSqlDto {
    @ApiModelProperty(value = "模板code", example = "101", required = true)
    @NotNull(message = "模板code不能为空")
    private String modelCode;
    @ApiModelProperty(value = "组件code", example = "201", required = true)
    @NotNull(message = "组件code不能为空")
    private String componentCode;
}
