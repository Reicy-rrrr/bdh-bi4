package com.deloitte.bdh.data.analyse.model.request;

import com.google.common.collect.Lists;
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
public class SaveResourcePermissionDto implements Serializable {

    @ApiModelProperty(value = "资源id，若是保存报表权限为page id，文件夹权限则为category id")
    private String id;

    @NotBlank
    @ApiModelProperty(value = "资源类型：'page'，'category'，'data_set'，'data_set_category'")
    private String resourceType;

    @ApiModelProperty(value = "查看权限用户")
    private List<String> viewUserList = Lists.newArrayList();

    @ApiModelProperty(value = "编辑权限用户")
    private List<String> editUserList = Lists.newArrayList();

}
