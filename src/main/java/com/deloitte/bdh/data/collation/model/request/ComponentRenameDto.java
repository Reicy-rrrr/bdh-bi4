package com.deloitte.bdh.data.collation.model.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 组件重命名dto
 *
 * @author chenghzhang
 * @date 2020-12-29
 */
@ApiModel(description = "组件重命名dto")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComponentRenameDto {

    @ApiModelProperty(value = "组件id", example = "10001", required = true)
    @NotNull(message = " 组件id 不能为空")
    private String componentId;

    @ApiModelProperty(value = "组件名称", example = "计算组件1", required = true)
    @NotNull(message = " 组件名称 不能为空")
    private String name;
}
