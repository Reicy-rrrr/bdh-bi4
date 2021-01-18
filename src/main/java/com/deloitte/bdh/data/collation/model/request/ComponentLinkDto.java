package com.deloitte.bdh.data.collation.model.request;


import com.deloitte.bdh.common.util.NifiProcessUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@ApiModel(description = "组件关联")
@Setter
@Getter
@ToString
public class ComponentLinkDto {

    @ApiModelProperty(value = "模板id", example = "0", required = true)
    @NotNull(message = " 模板id 不能为空")
    private String modelId;

    @ApiModelProperty(value = "关联组件编码", example = "0", required = true)
    @NotNull(message = " 关联组件编码 不能为空")
    private String fromComponentCode;

    @ApiModelProperty(value = "被关联组件编码", example = "0", required = true)
    @NotNull(message = " 被关联组件编码 不能为空")
    private String toComponentCode;

    @ApiModelProperty(value = "坐标", example = "1")
    private String position = NifiProcessUtil.randPosition();
}
