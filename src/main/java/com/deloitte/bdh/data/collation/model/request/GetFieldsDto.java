package com.deloitte.bdh.data.collation.model.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@ApiModel(description = "获取表所有的字段集合")
@Setter
@Getter
@ToString
public class GetFieldsDto {

    @ApiModelProperty(value = "数据源id", example = "123", required = true)
    @NotNull(message = "数据源id 不能为空")
    private String id;

    @ApiModelProperty(value = "tableName", example = "123", required = true)
    @NotNull(message = "tableName 不能为空")
    private String tableName;
}
