package com.deloitte.bdh.data.analyse.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UpdateAnalysePageConfigsDto {

    @ApiModelProperty(value = "配置ID")
    String id;

    @ApiModelProperty(value = "配置内容")
    String content;
}
