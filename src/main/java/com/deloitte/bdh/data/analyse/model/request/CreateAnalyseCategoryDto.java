package com.deloitte.bdh.data.analyse.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class CreateAnalyseCategoryDto {

    /**
     * 报表名称
     */
    @ApiModelProperty(value = "报表名称")
    private String name;

    /**
     * 报表描述
     */
    @ApiModelProperty(value = "报表描述")
    private String des;

    @NotBlank
    @ApiModelProperty(value = "上级id")
    private String parentId;

    @ApiModelProperty(value = "图标")
    private String icon;

    @NotBlank
    @ApiModelProperty(value = "文件夹类型，CUSTOMER、COMPONENT")
    private String type;
}
