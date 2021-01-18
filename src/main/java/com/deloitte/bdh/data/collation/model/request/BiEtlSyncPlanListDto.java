package com.deloitte.bdh.data.collation.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 文件预读取结果
 *
 * @author chenghzhang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BiEtlSyncPlanListDto extends PageDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 执行计划名称
     */
    @ApiModelProperty(value = "执行计划名称")
    private String name;

    /**
     * 执行计划类型（数据同步、数据整理）
     */
    @ApiModelProperty(value = "执行计划类型")
    private String planType;

    /**
     * 执行阶段（待执行、执行中、执行完成）
     */
    @ApiModelProperty(value = "执行阶段")
    private String planStage;

    /**
     * 执行结果（0-失败、1-成功、2-取消）
     */
    @ApiModelProperty(value = "执行结果")
    private String planResult;

    /**
     * 执行结果描述（失败、成功、取消）
     */
    @ApiModelProperty(value = "执行结果描述")
    private String resultDesc;

    /**
     * 模板code
     */
    @ApiModelProperty(value = "模板code")
    private String modelCode;

    /**
     * 模板名称
     */
    @ApiModelProperty(value = "模板名称")
    private String modelName;
}
