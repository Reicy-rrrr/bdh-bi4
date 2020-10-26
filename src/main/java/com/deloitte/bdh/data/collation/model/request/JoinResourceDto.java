package com.deloitte.bdh.data.collation.model.request;


import com.deloitte.bdh.data.collation.enums.SyncTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@ApiModel(description = "引入数据源 请求参数")
@Setter
@Getter
@ToString
public class JoinResourceDto extends BaseRequest {

    @ApiModelProperty(value = "modelId", example = "0", required = true)
    @NotNull(message = " 模板id 不能为空")
    private String modelId;

    @ApiModelProperty(value = "sourceId", example = "0", required = true)
    @NotNull(message = "数据源id 不能为空")
    private String sourceId;

    @ApiModelProperty(value = "tableName", example = "0")
    private String tableName;

    @ApiModelProperty(value = "同步方式", example = "0：直连，1：全量，2：增量")
    private Integer syncType = SyncTypeEnum.FULL.getKey();

    @ApiModelProperty(value = "偏移字段", example = "0")
    private String offsetField;

    @ApiModelProperty(value = "偏移量", example = "0")
    private String offsetValue;
}
