package com.deloitte.bdh.data.report.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UpdateReportDto {
    private String id;

    /**
     * 报表编码
     */
    @ApiModelProperty(value = "报表编码")
    private String code;

    /**
     * 报表名称
     */
    /**
     * 报表名称
     */
    @ApiModelProperty(value = "报表名称")
    private String name;

    @ApiModelProperty(value = "上级id")
    private String parentId;
}
