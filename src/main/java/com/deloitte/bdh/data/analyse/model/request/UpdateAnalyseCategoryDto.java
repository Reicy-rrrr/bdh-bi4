package com.deloitte.bdh.data.analyse.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UpdateAnalyseCategoryDto {
    private String id;

    /**
     * 报表编码
     */
    @ApiModelProperty(value = "报表编码")
    private String code;

    /**
     * 报表名称
     */
    /**
     * 报表名称
     */
    @ApiModelProperty(value = "报表名称")
    private String name;

    @ApiModelProperty(value = "上级id")
    private String parentId;

    @ApiModelProperty(value = "图标")
    private String icon;
}
