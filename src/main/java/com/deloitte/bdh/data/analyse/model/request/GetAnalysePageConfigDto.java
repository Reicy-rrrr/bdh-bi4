package com.deloitte.bdh.data.analyse.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
@ApiModel(description = "页面配置查询")
public class GetAnalysePageConfigDto {

    @ApiModelProperty(value = "pageId")
    @NotBlank
    private String pageId;

    @ApiModelProperty(value = "类型:EDIT,PUBLISH")
    private String type;
}
