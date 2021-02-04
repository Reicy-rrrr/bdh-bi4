package com.deloitte.bdh.data.analyse.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * Author:LIJUN
 * Date:03/02/2020
 * Description:
 */
@Data
public class GetPermissionByCodeDto implements Serializable {

    @NotBlank
    @ApiModelProperty(value = "报表code")
    private String code;

    @NotBlank
    @ApiModelProperty(value = "文件夹id")
    private String categoryId;

}
