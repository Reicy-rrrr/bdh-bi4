package com.deloitte.bdh.data.model.request;


import com.deloitte.bdh.common.util.NifiProcessUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@ApiModel(description = "引入数据源 请求参数")
@Setter
@Getter
@ToString
public class JoinResourceDto {

    @ApiModelProperty(value = "modelId", example = "0", required = true)
    @NotNull(message = " 模板id 不能为空")
    private String modelId;

    @ApiModelProperty(value = "sourceId", example = "0", required = true)
    @NotNull(message = "数据源id 不能为空")
    private String sourceId;

    @ApiModelProperty(value = "tableName", example = "0")
    private String tableName;

    @ApiModelProperty(value = "tenantId", example = "0", required = true)
    @NotNull(message = "租户id不能为空")
    private String tenantId;

    @ApiModelProperty(value = "createUser", example = "1", required = true)
    @NotNull(message = "createUser 不能为空")
    private String createUser;

    /**
     * 坐标
     */
    @ApiModelProperty(value = "坐标", example = "1")
    private String position = NifiProcessUtil.randPosition();
}
