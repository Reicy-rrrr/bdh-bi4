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
public class IssueDeloitteDto {


    @ApiModelProperty(value = "源PageId", required = true)
    @NotNull(message = "源PageId不能为空")
    private String fromPageId;

    @NotBlank
    @ApiModelProperty(value = "租户编码集合")
    private String tenantCodes;

    @ApiModelProperty(value = "文件夹名称", required = true)
    private String categoryName;

    @ApiModelProperty(value = "狗", required = true)
    private boolean withData;
}
