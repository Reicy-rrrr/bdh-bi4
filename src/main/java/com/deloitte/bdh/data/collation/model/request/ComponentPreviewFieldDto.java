package com.deloitte.bdh.data.collation.model.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 组件预览字段唯一值请求参数
 *
 * @author chenghzhang
 * @date 2020-11-25
 */
@ApiModel(description = "组件预览字段唯一值请求参数")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComponentPreviewFieldDto {
    @ApiModelProperty(value = "模板id", example = "10001", required = true)
    @NotNull(message = "模板id 不能为空")
    private String modelId;

    @ApiModelProperty(value = "组件id", example = "10001", required = true)
    @NotNull(message = "组件id 不能为空")
    private String componentId;

    @ApiModelProperty(value = "预览字段", example = "user_name", required = true)
    @NotNull(message = "field 不能为空")
    private String field;
}
