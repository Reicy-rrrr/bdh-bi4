package com.deloitte.bdh.data.collation.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;


@ApiModel(description = "基于租户获取Model列表请求参数")
@Setter
@Getter
@ToString
public class GetDataSetPageDto extends PageDto {

    @ApiModelProperty(value = "文件夹Id", example = "123", required = true)
    @NotNull(message = "文件夹Id 不能为空")
    private String fileId;
}
