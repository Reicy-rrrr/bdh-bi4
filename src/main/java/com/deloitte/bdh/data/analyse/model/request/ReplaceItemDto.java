package com.deloitte.bdh.data.analyse.model.request;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ReplaceItemDto {

    @ApiModelProperty(value = "数据集id", example = "3211231", required = true)
    @NotNull(message = "数据集id不能为空")
    private String fromDataSetCode;

    @ApiModelProperty(value = "数据集id", example = "3211231", required = true)
    @NotNull(message = "数据集id不能为空")
    private String toDataSetCode;
}
