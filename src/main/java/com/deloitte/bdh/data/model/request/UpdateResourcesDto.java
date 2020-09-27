package com.deloitte.bdh.data.model.request;

import com.deloitte.bdh.data.enums.SourceTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;


@ApiModel(description = "修改数据源")
@Setter
@Getter
@ToString
public class UpdateResourcesDto {

    @ApiModelProperty(value = "id", example = "123", required = true)
    @NotNull(message = "id 不能为空")
    private String id;

    @ApiModelProperty(value = "modifiedUser", example = "1", required = true)
    @NotNull(message = "modifiedUser 不能为空")
    private String modifiedUser;

    @ApiModelProperty(value = "数据源名称", example = "数据源名称", required = true)
    @NotNull(message = "数据源名称 不能为空")
    private String name;

    @ApiModelProperty(value = "描述", example = "描述", required = true)
    @NotNull(message = "描述 不能为空")
    private String comments;

    @ApiModelProperty(value = "数据源类型（1:mysql8.*;2:msql7.*;3:oracle）", required = true)
    @NotNull(message = "数据源类型 不能为空")
    private String type = SourceTypeEnum.Mysql_8.getType();

    @ApiModelProperty(value = "数据库名称", example = "1", required = true)
    private String dbName;

    @ApiModelProperty(value = "用户名", example = "1", required = true)
    private String dbUser;

    @ApiModelProperty(value = "密码", example = "1", required = true)
    private String dbPassword;

    @ApiModelProperty(value = "ip地址", example = "1", required = true)
    @NotNull(message = "ip地址 不能为空")
    private String address;

    @ApiModelProperty(value = "端口", example = "1", required = true)
    private String port;


}
