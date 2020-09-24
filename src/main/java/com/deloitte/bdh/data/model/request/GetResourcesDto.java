package com.deloitte.bdh.data.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description = "基于租户获取数据源列表请求参数")
@Setter
@Getter
@ToString
public class GetResourcesDto {

    @ApiModelProperty(value = "tenantId", example = "0", required = true)
    private String tenantId;

}
