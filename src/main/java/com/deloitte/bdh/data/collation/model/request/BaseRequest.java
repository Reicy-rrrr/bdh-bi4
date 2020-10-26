package com.deloitte.bdh.data.collation.model.request;

import com.deloitte.bdh.common.util.NifiProcessUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@ToString
public class BaseRequest {

    @ApiModelProperty(value = "tenantId", example = "0", required = true)
    @NotNull(message = "租户id不能为空")
    private String tenantId;

    @ApiModelProperty(value = "operator", example = "1", required = true)
    @NotNull(message = "operator 不能为空")
    private String operator;

    @ApiModelProperty(value = "坐标", example = "1")
    private String position = NifiProcessUtil.randPosition();
}
