package com.deloitte.bdh.data.analyse.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * Author:LIJUN
 * Date:05/11/2020
 * Description:
 */
@Data
public class CopyDeloittePageDto {

//    @NotBlank
//    @ApiModelProperty(value = "报表编码")
//    private String code;

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

    @ApiModelProperty(value = "源PageId", required = true)
    @NotNull(message = "源PageId不能为空")
    private String fromPageId;
}
