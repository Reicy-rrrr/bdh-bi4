package com.deloitte.bdh.data.model.request;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;


/**
 * @author chenghzhang
 */
@ApiModel(description = "文件数据源追加上传DTO")
@Setter
@Getter
@ToString
public class FileResourcesAddUploadDto {
    @ApiModelProperty(value = "数据源id", example = "10")
    @NotNull(message = "dbId 不能为空")
    private String dbId;

    @JSONField(serialize = false)
    @ApiModelProperty(value = "源文件", example = "example.xls")
    private MultipartFile file;

    @ApiModelProperty(value = "tenantId", example = "123", required = true)
    @NotNull(message = "租户id 不能为空")
    private String tenantId;

    @ApiModelProperty(value = "createUser", example = "1", required = true)
    @NotNull(message = "createUser 不能为空")
    private String createUser;
}
