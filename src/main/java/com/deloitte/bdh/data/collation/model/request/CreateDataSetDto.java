package com.deloitte.bdh.data.collation.model.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@ApiModel(description = "新增数据集")
@Setter
@Getter
@ToString
public class CreateDataSetDto {

    @ApiModelProperty(value = "文件夹ID", example = "0", required = true)
    @NotNull(message = "文件夹ID 不能为空")
    private String fileId;

    @ApiModelProperty(value = "数据源ID", example = "1", required = true)
    @NotNull(message = "数据源ID 不能为空")
    private String refSourceId;

    @ApiModelProperty(value = "表名称", example = "表名称", required = true)
    @NotNull(message = "表名称 不能为空")
    private String tableName;

}
