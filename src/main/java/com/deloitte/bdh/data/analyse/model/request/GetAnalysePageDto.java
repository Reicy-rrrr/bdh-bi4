package com.deloitte.bdh.data.analyse.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GetAnalysePageDto {

    @ApiModelProperty(value = "page id")
    @NotNull(message = "报表id")
    private String pageId;

    @ApiModelProperty(value = "1")
    private String fromDeloitte;
}
