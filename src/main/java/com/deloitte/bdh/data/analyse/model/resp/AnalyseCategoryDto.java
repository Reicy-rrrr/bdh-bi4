package com.deloitte.bdh.data.analyse.model.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Author:LIJUN
 * Date:12/11/2020
 * Description:
 */
@Data
public class AnalyseCategoryDto {

    @ApiModelProperty(value = "ID")
    private String id;

    @ApiModelProperty(value = "CODE")
    private String code;

    @ApiModelProperty(value = "NAME")
    private String name;

    @ApiModelProperty(value = "TYPE")
    private String type;

    @ApiModelProperty(value = "PARENT_ID")
    private String parentId;

    @ApiModelProperty(value = "DES")
    private String des;

    @ApiModelProperty(value = "ICON")
    private String icon;

    @ApiModelProperty(value = "permitted Action")
    private String permittedAction;

}
