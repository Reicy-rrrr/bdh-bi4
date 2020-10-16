package com.deloitte.bdh.data.collation.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@ApiModel(description = "基于租户获取数据源列表请求参数")
@Setter
@Getter
@ToString
public class GetResourcesDto extends PageDto {

    @ApiModelProperty(value = "tenantId", example = "0", required = true)
    @NotNull(message = "租户id不能为空")
    private String tenantId;

}
