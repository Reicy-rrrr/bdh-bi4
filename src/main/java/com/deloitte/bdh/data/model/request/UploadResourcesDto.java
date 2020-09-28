package com.deloitte.bdh.data.model.request;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;


@ApiModel(description = "上传数据源")
@Setter
@Getter
@ToString
public class UploadResourcesDto {

    @ApiModelProperty(value = "tenantId", example = "123", required = true)
    @NotNull(message = "租户id 不能为空")
    private String tenantId;

    @ApiModelProperty(value = "数据源名称", example = "数据源名称", required = true)
    @NotNull(message = "数据源名称 不能为空")
    private String name;

    @ApiModelProperty(value = "描述", example = "描述")
    private String comments;

    @JSONField(serialize = false)
    @ApiModelProperty(value = "源文件", example = "源文件")
    private MultipartFile file;

    @ApiModelProperty(value = "createUser", example = "1", required = true)
    @NotNull(message = "createUser 不能为空")
    private String createUser;
}
