package com.deloitte.bdh.data.nifi.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


@ApiModel(description = "新增 Connection")
@Setter
@Getter
@ToString
public class CreateConnectionDto implements Serializable {
    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "createUser", example = "1", required = true)
    @NotNull(message = "createUser 不能为空")
    private String createUser;


    @ApiModelProperty(value = "tenantId", example = "1", required = true)
    @NotNull(message = "tenantId 不能为空")
    private String tenantId;

    /**
     * 关联Processor编码
     */
    @ApiModelProperty(value = "fromProcessorCode", example = "1", required = true)
    @NotNull(message = "fromProcessorCode 不能为空")
    private String fromProcessorCode;

    /**
     * 被关联Processor编码
     */
    @ApiModelProperty(value = "被关联Processor编码", example = "1", required = true)
    @NotNull(message = "被关联Processor编码 不能为空")
    private String toProcessorCode;

    /**
     * relCode
     */
    @ApiModelProperty(value = "relCode", example = "1")
    private String relCode;

    /**
     * modelCode
     */
    @ApiModelProperty(value = "modelCode", example = "1")
    private String modelCode;
}
