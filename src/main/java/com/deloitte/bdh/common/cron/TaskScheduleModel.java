package com.deloitte.bdh.common.cron;


import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@ApiModel(description = "cron表达体")
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class TaskScheduleModel extends Model<TaskScheduleModel> {

    /**
     * 所选作业类型:
     * 0  -> 分钟
     * 1  -> 每天
     * 2  -> 每月
     * 3  -> 每周
     * 4  —> 小时
     */
    @ApiModelProperty(value = "作业类型,0  -> 分钟,1  -> 每天,2  -> 每月,3  -> 每周,4  —> 小时", example = "1", required = true)
    @NotNull(message = "作业类型 不能为空")
    Integer jobType;

    /**
     * 一周的哪几天
     */
    @ApiModelProperty(value = "一周的哪几天", example = "[1,2]", required = true)
    Integer[] dayOfWeeks;

    /**
     * 一个月的哪几天
     */
    @ApiModelProperty(value = "一个月的哪几天", example = "[1,2]", required = true)
    Integer[] dayOfMonths;

    /**
     * 秒
     */
    @ApiModelProperty(value = "秒", example = "1", required = true)
    Integer second;

    /**
     * 分
     */
    @ApiModelProperty(value = "分", example = "1", required = true)
    Integer minute;

    /**
     * 时
     */
    @ApiModelProperty(value = "时", example = "1", required = true)
    Integer hour;

}

