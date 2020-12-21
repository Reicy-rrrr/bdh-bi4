package com.deloitte.bdh.data.analyse.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Author:LIJUN
 * Date:21/12/2020
 * Description:
 */
@Data
public class UploadDto {

    @NotBlank
    @ApiModelProperty(value = "报表id")
    private String pageId;

    @NotBlank
    @ApiModelProperty(value = "租户id")
    private String tenantId;

    @NotBlank
    @ApiModelProperty(value = "操作用户")
    private String operator;

}
