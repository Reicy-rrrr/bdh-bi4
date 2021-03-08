package com.deloitte.bdh.common.client.dto;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Author:LIJUN
 * Date:08/03/2021
 * Description:
 */
@Data
@ApiModel(value = "")
public class SelectOrgPosWithEmpDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ORGANIZATION,POSITION,USER")
    private String type;

    @ApiModelProperty(value = "superuser,editor,viewer,visitor")
    private List<String> roleType;

    @ApiModelProperty(value = "用户列表")
    private List<String> userList = Lists.newArrayList();

    @ApiModelProperty(value = "组织id")
    private List<String> organizationList = Lists.newArrayList();
}
