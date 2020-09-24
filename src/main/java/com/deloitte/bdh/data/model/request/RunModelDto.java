package com.deloitte.bdh.data.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@ApiModel(description = "启用/停止MODEL")
@Setter
@Getter
@ToString
public class RunModelDto {
    @ApiModelProperty(value = "数据源id", example = "123", required = true)
    @NotNull(message = "数据源id 不能为空")
    private String id;

    @ApiModelProperty(value = "启用/禁用", example = "ENABLE/DISABLE", required = true)
    @NotNull(message = "状态 不能为空")
    private String effect;

    @ApiModelProperty(value = "modifiedUser", example = "0", required = true)
    @NotNull(message = "modifiedUser 不能为空")
    private String modifiedUser;
}
