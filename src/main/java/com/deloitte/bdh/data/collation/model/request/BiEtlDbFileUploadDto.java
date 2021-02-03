package com.deloitte.bdh.data.collation.model.request;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author chenghzhang
 */
@ApiModel(description = "文件上传DTO")
@Setter
@Getter
@ToString
public class BiEtlDbFileUploadDto {
    @JSONField(serialize = false)
    @ApiModelProperty(value = "上传文件", example = "example.xls", required = true)
    @NotNull(message = "上传文件 不能为空")
    private MultipartFile file;

    @ApiModelProperty(value = "租户id", example = "1001", required = true)
    @NotNull(message = "租户id 不能为空")
    private String tenantId;

    @ApiModelProperty(value = "租户code", example = "1001", required = true)
    @NotNull(message = "租户code 不能为空")
    private String tenantCode;

    @ApiModelProperty(value = "操作用户id", example = "1010", required = true)
    @NotNull(message = "操作用户id 不能为空")
    private String operator;

    @ApiModelProperty(value = "国际化语言标识", example = "cn")
    private String lang;

    @ApiModelProperty(value = "表列表", example = "1", required = true)
    @NotNull(message = "表列表 不能为空")
    private List<String> tables;

}
