package com.deloitte.bdh.data.analyse.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

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
}
