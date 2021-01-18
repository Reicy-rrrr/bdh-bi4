package com.deloitte.bdh.data.collation.model.request;

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
public class UpdateModelContent {

    @ApiModelProperty(value = "id", example = "123", required = true)
    @NotNull(message = "id 不能为空")
    private String id;


    @ApiModelProperty(value = "content", example = "content")
    @NotNull(message = "content")
    private String content;

}
