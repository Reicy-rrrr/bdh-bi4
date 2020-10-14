package com.deloitte.bdh.data.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.LinkedHashMap;


/**
 * @author chenghzhang
 */
@ApiModel(description = "文件数据源保存DTO")
@Setter
@Getter
@ToString
public class FileResourcesSaveDto {
    @ApiModelProperty(value = "数据源名称", example = "数据源名称", required = true)
    @NotNull(message = "租户id 不能为空")
    private String dbId;

    @ApiModelProperty(value = "数据源名称", example = "数据源名称", required = true)
    @NotNull(message = "数据源名称 不能为空")
    private String fileId;

    @ApiModelProperty(value = "字段类型", example = "id:Integer, code:String, value:Float", required = true)
    @NotNull(message = "字段类型 不能为空")
    private LinkedHashMap<String, String> columns;

    @ApiModelProperty(value = "tenantId", example = "123", required = true)
    @NotNull(message = "租户id 不能为空")
    private String tenantId;

    @ApiModelProperty(value = "createUser", example = "1", required = true)
    @NotNull(message = "createUser 不能为空")
    private String createUser;
}
