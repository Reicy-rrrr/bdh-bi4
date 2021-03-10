package com.deloitte.bdh.data.analyse.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Author:LIJUN
 * Date:05/11/2020
 * Description:
 */
@Data
public class IssueDeloittePageDto {


    @ApiModelProperty(value = "源PageId", required = true)
    @NotNull(message = "源PageId不能为空")
    private String fromPageId;

    @NotBlank
    @ApiModelProperty(value = "租户列表")
    private Map<String, IssueTenantDto> dtos;
}
