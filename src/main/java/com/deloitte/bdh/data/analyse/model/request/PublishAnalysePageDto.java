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
public class PublishAnalysePageDto implements Serializable {

    @NotBlank
    @ApiModelProperty(value = "pageId")
    String pageId;

    @NotBlank
    @ApiModelProperty(value = "categoryId")
    String categoryId;

    @ApiModelProperty(value = "配置ID")
    String configId;

    @NotBlank
    @ApiModelProperty(value = "配置内容")
    String content;

    @ApiModelProperty(value = "可见编辑权限配置")
    SaveResourcePermissionDto saveResourcePermissionDto;

    @ApiModelProperty(value = "数据权限配置项")
    private List<PermissionItemDto> permissionItemDtoList;

}
