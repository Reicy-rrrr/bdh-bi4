package com.deloitte.bdh.data.analyse.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * Author:LIJUN
 * Date:29/12/2020
 * Description:
 */
@Data
public class GetResourcePermissionDto implements Serializable {

    @ApiModelProperty(value = "名称")
    private String name;

    @NotBlank
    @ApiModelProperty(value = "资源id，若是保存报表权限为page id，文件夹权限则为category id")
    private String id;

    @NotBlank
    @ApiModelProperty(value = "资源类型：'page'，'category'，'data_set'，'data_set_category'")
    private String resourceType;

}
