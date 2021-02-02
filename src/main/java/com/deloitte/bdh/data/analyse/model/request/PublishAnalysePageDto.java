package com.deloitte.bdh.data.analyse.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;
import java.util.List;

/**
 * Author:LIJUN
 * Date:09/12/2020
 * Description:
 */
@Data
public class PublishAnalysePageDto implements Serializable {

    @NotBlank
    @ApiModelProperty(value = "pageId")
    String pageId;

    @NotBlank(message = "发布页面，字段不能为null")
    @ApiModelProperty(value = "categoryId" ,required = true)
    String categoryId;

    @ApiModelProperty(value = "配置ID")
    String configId;

    @ApiModelProperty(value = "配置内容")
    String content;

    @ApiModelProperty(value = "是否是公开报表,1：是；0：不是")
    String isPublic;

    @ApiModelProperty(value = "报表密码")
    String password;

    @ApiModelProperty(value = "是否是德勤方案")
    String deloitteFlag;

    @ApiModelProperty(value = "可见编辑权限配置")
    SaveResourcePermissionDto saveResourcePermissionDto;

    @ApiModelProperty(value = "数据权限配置项")
    private List<PermissionItemDto> permissionItemDtoList;

}
