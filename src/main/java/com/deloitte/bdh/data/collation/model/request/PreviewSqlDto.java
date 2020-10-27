package com.deloitte.bdh.data.collation.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * 预览SQL
 *
 * @author chenghzhang
 * @date 2020/10/26
 */
@ApiModel(description = "预览")
@Setter
@Getter
@ToString
public class PreviewSqlDto {
    @ApiModelProperty(value = "模板id", example = "101", required = true)
    @NotNull(message = "模板id不能为空")
    private String modelId;
    @ApiModelProperty(value = "组件id", example = "201", required = true)
    @NotNull(message = "组件id不能为空")
    private String componentId;
}
