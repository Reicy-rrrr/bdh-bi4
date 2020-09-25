package com.deloitte.bdh.data.model.request;

import com.deloitte.bdh.common.util.NifiProcessUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author lw
 * @since 2020-09-25
 */
@ApiModel(description = "修改process 请求参数")
@Setter
@Getter
@ToString
public class UpdateProcessorDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id", example = "0", required = true)
    @NotNull(message = " id 不能为空")
    private String id;

    /**
     * 处理器名称
     */
    @ApiModelProperty(value = "处理器名称", example = "0")
    private String name;

    /**
     * 坐标
     */
    @ApiModelProperty(value = "position", example = "0")
    private String position = NifiProcessUtil.randPosition();

    @ApiModelProperty(value = "modifiedUser", example = "0", required = true)
    @NotNull(message = " modifiedUser 不能为空")
    private String modifiedUser;

    @ApiModelProperty(value = "tenantId", example = "0", required = true)
    @NotNull(message = " tenantId 不能为空")
    private String tenantId;
}
