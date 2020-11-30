package com.deloitte.bdh.data.collation.model.resp;


import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceViewResp {


    /**
     * 执行阶段（待执行、执行中、执行完成）
     */
    @ApiModelProperty(value = "执行阶段")
    private String planStage;

    /**
     * 执行阶段（待执行、执行中、执行完成）
     */
    @ApiModelProperty(value = "执行阶段描述")
    private String planStageDesc;

    /**
     * 执行结果（0、1、2）
     */
    @ApiModelProperty(value = "0、1、2")
    private String planResult;

    /**
     * 执行结果描述（失败、成功）
     */
    @ApiModelProperty(value = "执行结果描述")
    private String resultDesc;

    /**
     * 执行SQL总条数
     */
    @ApiModelProperty(value = "执行SQL总条数")
    private String sqlCount;

    /**
     * 执行SQL本地总条数
     */
    @ApiModelProperty(value = "执行SQL本地总条数")
    private String sqlLocalCount;

    /**
     * 同步进度
     */
    @ApiModelProperty(value = "同步进度")
    private String progressRate;

    @ApiModelProperty(value = "组件是否可以用")
    private String effect;
}
