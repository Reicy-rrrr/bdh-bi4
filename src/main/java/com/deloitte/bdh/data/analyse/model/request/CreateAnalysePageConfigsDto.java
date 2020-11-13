package com.deloitte.bdh.data.analyse.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CreateAnalysePageConfigsDto {

    @ApiModelProperty(value = "报表ID")
    String pageId;

    @ApiModelProperty(value = "报表内容")
    String content;
}
