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
public class PermissionItemDto implements Serializable {

    @ApiModelProperty(value = "表")
    @NotBlank
    private String tableName;

    @ApiModelProperty(value = "字段")
    @NotBlank
    private String tableField;

    @ApiModelProperty(value = "值")
    @NotBlank
    private String fieldValue;

    @NotEmpty
    @ApiModelProperty(value = "查看权限用户")
    private List<String> userList;

}
