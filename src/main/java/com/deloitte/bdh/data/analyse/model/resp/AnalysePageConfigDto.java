package com.deloitte.bdh.data.analyse.model.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Author:LIJUN
 * Date:12/11/2020
 * Description:
 */
@Data
public class AnalysePageConfigDto {

    @ApiModelProperty(value = "ID")
    private String id;

    @ApiModelProperty(value = "PAGE_ID")
    private String pageId;

    @ApiModelProperty(value = "CONTENT")
    private String content;

}
