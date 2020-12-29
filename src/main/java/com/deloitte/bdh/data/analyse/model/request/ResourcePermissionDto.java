package com.deloitte.bdh.data.analyse.model.request;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;
import java.util.List;

/**
 * Author:LIJUN
 * Date:29/12/2020
 * Description:
 */
@Data
public class ResourcePermissionDto implements Serializable {

    @ApiModelProperty(value = "查看权限用户")
    private List<String> viewUserList = Lists.newArrayList();

    @ApiModelProperty(value = "编辑权限用户")
    private List<String> editUserList = Lists.newArrayList();

}
