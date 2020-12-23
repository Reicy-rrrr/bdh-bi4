package com.deloitte.bdh.data.collation.model.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@ApiModel(description = "预览表字段值 请求参数")
@Setter
@Getter
@ToString
public class ViewFieldValueDto {

    @ApiModelProperty(value = "sourceId", example = "0", required = true)
    @NotNull(message = "数据源id 不能为空")
    private String sourceId;

    @ApiModelProperty(value = "tableName", example = "0")
    @NotNull(message = "tableName 不能为空")
    private String tableName;

    @ApiModelProperty(value = "field", example = "0")
    @NotNull(message = "field 不能为空")
    private String field;
}
