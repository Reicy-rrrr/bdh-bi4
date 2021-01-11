package com.deloitte.bdh.data.collation.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * @author chenghzhang
 */
@ApiModel(description = "文件数据源上传DTO")
@Setter
@Getter
@ToString
public class CreateFileResourcesDto {
    @ApiModelProperty(value = "数据源名称", example = "xxx数据库", required = true)
    @NotNull(message = "数据源名称 不能为空")
    private String name;

    @ApiModelProperty(value = "描述", example = "xxx的描述")
    private String comments;

    @ApiModelProperty(value = "文件信息id", example = "11", required = true)
    @NotNull(message = "文件信息id 不能为空")
    private String fileId;

    @ApiModelProperty(value = "字段类型", example = "id:1, code:2, value:3", required = true)
    @NotNull(message = "字段类型 不能为空")
    private LinkedHashMap<String, String> columns;
}
