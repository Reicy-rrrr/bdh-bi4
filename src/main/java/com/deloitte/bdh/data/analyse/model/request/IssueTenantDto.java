package com.deloitte.bdh.data.analyse.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class IssueTenantDto {
    @NotBlank
    @ApiModelProperty(value = "报表名称")
    private String name;

    @NotBlank
    @ApiModelProperty(value = "文件夹id")
    private String categoryId;

    @NotBlank
    @ApiModelProperty(value = "数据集文件夹id")
    private String dataSetCategoryId;

    @NotBlank
    @ApiModelProperty(value = "数据集名称")
    private String dataSetName;

}
