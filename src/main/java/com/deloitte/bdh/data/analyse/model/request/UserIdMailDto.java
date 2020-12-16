package com.deloitte.bdh.data.analyse.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Author:LIJUN
 * Date:14/12/2020
 * Description:
 */
@Data
public class UserIdMailDto {

    @NotBlank
    @ApiModelProperty(value = "用户ID")
    private String userId;

    @NotBlank
    @ApiModelProperty(value = "用户昵称")
    private String userName;

    @NotBlank
    @ApiModelProperty(value = "邮箱")
    private String email;

}
