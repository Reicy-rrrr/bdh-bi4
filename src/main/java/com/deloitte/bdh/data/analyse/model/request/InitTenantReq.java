package com.deloitte.bdh.data.analyse.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class InitTenantReq {
    @ApiModelProperty(value = "tenantId", example = "123", required = true)
    @NotNull(message = "租户id 不能为空")
    private String tenantId;
}
