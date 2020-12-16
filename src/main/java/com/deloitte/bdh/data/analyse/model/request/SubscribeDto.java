package com.deloitte.bdh.data.analyse.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * Author:LIJUN
 * Date:14/12/2020
 * Description:
 */
@Data
public class SubscribeDto {

    @NotBlank
    @ApiModelProperty(value = "报表")
    private String pageId;

    @NotBlank
    @ApiModelProperty(value = "邮件主题")
    private String mailSubject;

    @NotBlank
    @ApiModelProperty(value = "定时配置")
    private String cronData;

    @NotEmpty
    @ApiModelProperty(value = "收件人")
    private List<UserIdMailDto> receiver;

    @NotBlank
    @ApiModelProperty(value = "状态：1-启用，0-禁用")
    private String status;

}
