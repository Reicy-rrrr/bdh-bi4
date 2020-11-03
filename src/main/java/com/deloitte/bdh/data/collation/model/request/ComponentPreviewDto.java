package com.deloitte.bdh.data.collation.model.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 组件预览请求参数
 *
 * @author chenghzhang
 * @date 2020-11-03
 */
@ApiModel(description = "组件预览请求参数")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComponentPreviewDto {
    @ApiModelProperty(value = "modelId", example = "10001", required = true)
    @NotNull(message = "模板id 不能为空")
    private String modelId;

    @ApiModelProperty(value = "componentId", example = "10001", required = true)
    @NotNull(message = "组件id 不能为空")
    private String componentId;
}
