package com.deloitte.bdh.data.analyse.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;
import java.util.List;

/**
 * Author:LIJUN
 * Date:09/12/2020
 * Description:
 */
@Data
public class PermissionUserDto implements Serializable {

    @NotBlank
    @ApiModelProperty(value = "查看权限用户")
    private String userId;

    @NotEmpty
    @ApiModelProperty(value = "可查看的字段值")
    private List<String> fieldValueList;

}
