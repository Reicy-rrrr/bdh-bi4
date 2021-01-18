package com.deloitte.bdh.data.analyse.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GetAnalysePageDto {

    @ApiModelProperty(value = "categoryId")
    @NotNull(message = "目录id")
    private String categoryId;

    @ApiModelProperty(value = "名称")
    private String name;
}
