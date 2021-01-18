package com.deloitte.bdh.data.analyse.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * Author:LIJUN
 * Date:05/11/2020
 * Description:
 */
@Data
public class CopyAnalysePageDto {
    /**
     * 报表编码
     */
    @NotBlank
    @ApiModelProperty(value = "报表编码")
    private String code;

    /**
     * 报表名称
     */
    @NotBlank
    @ApiModelProperty(value = "报表名称")
    private String name;

    /**
     * 报表描述
     */
    @ApiModelProperty(value = "报表描述")
    private String des;

    @NotBlank
    @ApiModelProperty(value = "上级id")
    private String parentId;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "tenantId", example = "123", required = true)
    @NotNull(message = "租户id 不能为空")
    private String tenantId;

    @ApiModelProperty(value = "创建人", required = true)
    @NotNull(message = "创建人不能为空")
    private String createUser;

    @ApiModelProperty(value = "源PageId", required = true)
    @NotNull(message = "源PageId不能为空")
    private String fromPageId;
}
