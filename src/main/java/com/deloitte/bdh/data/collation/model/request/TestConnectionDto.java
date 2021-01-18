package com.deloitte.bdh.data.collation.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@ApiModel(description = "测试连接")
@Setter
@Getter
@ToString
public class TestConnectionDto {
    @ApiModelProperty(value = "ip", example = "123", required = true)
    @NotNull(message = "ip 不能为空")
    private String ip;

    @ApiModelProperty(value = "port", example = "123", required = true)
    @NotNull(message = "port 不能为空")
    private String port;

    @ApiModelProperty(value = "dbUserName", example = "123", required = true)
    @NotNull(message = "dbUserName 不能为空")
    private String dbUserName;

    @ApiModelProperty(value = "dbPassword", example = "123", required = true)
    @NotNull(message = "dbPassword 不能为空")
    private String dbPassword;

    @ApiModelProperty(value = "dbName", example = "123", required = true)
    @NotNull(message = "dbName 不能为空")
    private String dbName;

    @ApiModelProperty(value = "dbName", example = "123", required = true)
    @NotNull(message = "dbName 不能为空")
    private String dbType;
}
