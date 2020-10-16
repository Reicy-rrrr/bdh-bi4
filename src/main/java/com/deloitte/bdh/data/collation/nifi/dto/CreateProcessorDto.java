package com.deloitte.bdh.data.collation.nifi.dto;

import com.deloitte.bdh.common.util.NifiProcessUtil;
import com.deloitte.bdh.data.collation.model.BiProcessors;
import com.google.common.collect.Maps;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;


@ApiModel(description = "新增 Processor")
@Setter
@Getter
@ToString
public class CreateProcessorDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 处理器名称
     */
    @ApiModelProperty(value = "处理器名称", example = "123", required = true)
    @NotNull(message = "处理器名称 不能为空")
    private String name;

    /**
     * 类型
     */
    @ApiModelProperty(value = "处理器类型", example = "1", required = true)
    @NotNull(message = "处理器类型 不能为空")
    private String type;

    /**
     * 坐标
     */
    @ApiModelProperty(value = "坐标", example = "1")
    private String position = NifiProcessUtil.randPosition();


    @ApiModelProperty(value = "createUser", example = "1", required = true)
    @NotNull(message = "createUser 不能为空")
    private String createUser;


    @ApiModelProperty(value = "tenantId", example = "1", required = true)
    @NotNull(message = "tenantId 不能为空")
    private String tenantId;


    /**
     * processors
     */
    @ApiModelProperty(value = "processors", example = "1", required = true)
    @NotNull(message = "processors")
    private BiProcessors processors;

    /**
     * sequence
     */
    @ApiModelProperty(value = "sequence", example = "1")
    private String sequence = "0";


    @ApiModelProperty(value = "params", example = "1")
    private Map<String, Object> params = Maps.newHashMap();
}
