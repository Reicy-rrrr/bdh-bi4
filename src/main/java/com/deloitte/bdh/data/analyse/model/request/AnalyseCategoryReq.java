package com.deloitte.bdh.data.analyse.model.request;

import com.deloitte.bdh.data.collation.model.request.PageDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "文件夹/报表管理")
public class AnalyseCategoryReq extends PageDto {
    @ApiModelProperty(value = "tenantId", example = "123", required = true)
    @NotNull(message = "租户id 不能为空")
    private String tenantId;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "系统初始化,自建")
    private String initType;

    @ApiModelProperty(value = "我的分析,预定义报表")
    private String type;
}
