package com.deloitte.bdh.data.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;


@ApiModel(description = "修改Model基本配置")
@Setter
@Getter
@ToString
public class UpdateModelDto {

    @ApiModelProperty(value = "id", example = "123", required = true)
    @NotNull(message = "id 不能为空")
    private String id;

    @ApiModelProperty(value = "modifiedUser", example = "1", required = true)
    @NotNull(message = "modifiedUser 不能为空")
    private String modifiedUser;

    @ApiModelProperty(value = "数据源名称", example = "数据源名称")
    private String name;

    @ApiModelProperty(value = "描述", example = "描述")
    private String comments;




}
