package com.deloitte.bdh.data.analyse.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AnalysePublicShareDto {
    @ApiModelProperty(value = "关联报表ID", example = "10", required = true)
    @NotNull(message = "关联报表ID 不能为空")
    private String pageId;

    @ApiModelProperty(value = "公开类型（0：不公开，1：公开，2：加密公开）", example = "2", required = true)
    @NotNull(message = "公开类型 不能为空")
    private String type;


    @ApiModelProperty(value = "密码", example = "10", required = true)
    private String password;

}
