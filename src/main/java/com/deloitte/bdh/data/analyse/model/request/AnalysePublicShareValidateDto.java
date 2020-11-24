package com.deloitte.bdh.data.analyse.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AnalysePublicShareValidateDto {
    @ApiModelProperty(value = "关联报表ID", example = "10", required = true)
    @NotNull(message = "关联报表ID 不能为空")
    private String pageId;

    @ApiModelProperty(value = "密码", example = "10", required = true)
    @NotNull(message = "密码 不能为空")
    private String password;

}
