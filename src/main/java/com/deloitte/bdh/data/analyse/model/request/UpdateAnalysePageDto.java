package com.deloitte.bdh.data.analyse.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UpdateAnalysePageDto {

    private String id;

    @ApiModelProperty(value = "报表编码")
    private String code;

    @ApiModelProperty(value = "报表名称")
    private String name;

    @ApiModelProperty(value = "描述")
    private String des;

    @ApiModelProperty(value = "上级id")
    private String parentId;

    @ApiModelProperty(value = "图标")
    private String icon;
}
