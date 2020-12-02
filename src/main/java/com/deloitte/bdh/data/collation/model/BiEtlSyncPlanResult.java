package com.deloitte.bdh.data.collation.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件预读取结果
 *
 * @author chenghzhang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BiEtlSyncPlanResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 计划id
     */
    @JsonIgnore
    @ApiModelProperty(value = "计划id")
    private String id;

    /**
     * 计划编码
     */
    @JsonIgnore
    @ApiModelProperty(value = "计划编码")
    private String code;

    /**
     * 计划名称
     */
    @ApiModelProperty(value = "计划名称")
    private String name;

    /**
     * 计划组编码
     */
    @JsonIgnore
    @ApiModelProperty(value = "计划组编码")
    private String groupCode;

    /**
     * 执行计划类型（数据同步、数据整理）
     */
    @ApiModelProperty(value = "执行计划类型")
    private String planType;

    /**
     * 执行计划类型描述（数据同步、数据整理）
     */
    @ApiModelProperty(value = "执行计划类型描述")
    private String planTypeDesc;

    /**
     * 执行阶段（待执行、执行中、执行完成）
     */
    @ApiModelProperty(value = "执行阶段")
    private String planStage;

    /**
     * 执行阶段描述（待执行、执行中、执行完成）
     */
    @ApiModelProperty(value = "执行阶段描述")
    private String planStageDesc;

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
     * 执行SQL总条数
     */
    @JsonIgnore
    @ApiModelProperty(value = "执行SQL总条数")
    private String sqlCount;

    /**
     * 执行SQL本地总条数
     */
    @JsonIgnore
    @ApiModelProperty(value = "执行SQL本地总条数")
    private String sqlLocalCount;

    /**
     * 执行百分百
     */
    @JsonIgnore
    @ApiModelProperty(value = "执行百分百")
    private String percentage;

    /**
     * 上次执行时间
     */
    @ApiModelProperty(value = "上次执行时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lastExecuteDate;

    /**
     * 下次执行时间
     */
    @ApiModelProperty(value = "下次执行时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime nextExecuteDate;

    /**
     * 计划执行时间
     */
    @ApiModelProperty(value = "计划执行时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime currExecuteDate;

    /**
     * 实际执行时间
     */
    @ApiModelProperty(value = "实际执行时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime actualExecuteDate;

    /**
     * 模板code
     */
    @JsonIgnore
    @ApiModelProperty(value = "模板code")
    private String modelCode;

    /**
     * 模板名称
     */
    @ApiModelProperty(value = "模板名称")
    private String modelName;
}
