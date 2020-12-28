package com.deloitte.bdh.data.analyse.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "文件夹/报表管理")
public class GetAnalyseCategoryDto {

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "我的分析(CUSTOMER),图形指标库(COMPONENT)")
    private String type;

}
