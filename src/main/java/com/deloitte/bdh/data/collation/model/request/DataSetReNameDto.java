package com.deloitte.bdh.data.collation.model.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@ApiModel(description = "数据集修改表名称")
@Setter
@Getter
@ToString
public class DataSetReNameDto {

    @ApiModelProperty(value = "模板编码", example = "3211231", required = true)
    @NotNull(message = "模板编码 不能为空")
    private String code;

    @ApiModelProperty(value = "表名称", example = "表名称", required = true)
    @NotNull(message = "表名称 不能为空")
    private String toTableDesc;
}
