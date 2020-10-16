package com.deloitte.bdh.data.collation.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@ApiModel(description = "预览")
@Setter
@Getter
@ToString
public class PreviewDto {
    @ApiModelProperty(value = "模板id", example = "123", required = true)
    @NotNull(message = "模板id 不能为空")
    private String id;

    @ApiModelProperty(value = "modifiedUser", example = "0", required = true)
    @NotNull(message = "modifiedUser 不能为空")
    private String modifiedUser;

    @ApiModelProperty(value = "previewCode", example = "0", required = true)
    @NotNull(message = "previewCode 不能为空")
    private String previewCode;
}
