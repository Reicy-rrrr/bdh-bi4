package com.deloitte.bdh.data.analyse.model.request;

import com.deloitte.bdh.data.collation.model.request.PageDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "页面配置查询")
public class AnalysePageConfigDto {
    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "pageId")
    private String pageId;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "类型,EDIT,PUBLISH 默认是为edit")
    private String type;
}
