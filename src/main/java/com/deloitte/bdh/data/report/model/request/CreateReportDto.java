package com.deloitte.bdh.data.report.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CreateReportDto {
    /**
     * 报表编码
     */
    @ApiModelProperty(value = "报表编码")
    private String code;

    /**
     * 报表名称
     */
    @ApiModelProperty(value = "报表名称")
    private String name;

    /**
     * 文件夹/报表/dashboard
     */
    @ApiModelProperty(value = "文件夹/报表/dashboard")
    private String type;

    /**
     * 是否用户自定义类型CUSTOMER,TYPE1,TYPE2...
     */
    @ApiModelProperty(value = "是否用户自定义类型CUSTOMER,TYPE1,TYPE2")
    private String initType;
    /**
     * 报表描述
     */
    @ApiModelProperty(value = "报表描述")
    private String des;

    @ApiModelProperty(value = "上级id")
    private String parentId;
}
