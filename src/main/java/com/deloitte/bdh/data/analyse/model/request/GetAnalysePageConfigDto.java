package com.deloitte.bdh.data.analyse.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
@ApiModel(description = "页面配置查询")
public class GetAnalysePageConfigDto {

    @ApiModelProperty(value = "pageId")
    private String pageId;

    @ApiModelProperty(value = "类型:EDIT,PUBLISH")
    private String type;

    @ApiModelProperty(value = "configId")
    private String configId;

    @ApiModelProperty(value = "1")
    private String fromDeloitte;
}
