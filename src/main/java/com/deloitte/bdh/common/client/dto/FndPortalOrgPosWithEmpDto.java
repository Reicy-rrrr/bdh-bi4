package com.deloitte.bdh.common.client.dto;

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
public class FndPortalOrgPosWithEmpDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "中文名称")
    private String name;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "ORGANIZATION,POSITION,USER")
    private String type;

    @ApiModelProperty(value = "上级id")
    private String parentId;

    @ApiModelProperty(value = "下级数据")
    List<FndPortalOrgPosWithEmpDto> children;
}
