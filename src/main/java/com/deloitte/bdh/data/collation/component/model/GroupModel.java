package com.deloitte.bdh.data.collation.component.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 聚合模型
 *
 * @author chenghzhang
 * @date 2020/11/03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupModel {
    /** 分组的字段 */
    @ApiModelProperty(value = "分组的字段", example = "order_id, order_code", required = true)
    private List<String> group;

    /** 计算最大值的字段 */
    @ApiModelProperty(value = "计算最大值的字段", example = "total_amount, pay_amount", required = true)
    private List<String> max;

    /** 计算最小值的字段 */
    @ApiModelProperty(value = "计算最小值的字段", example = "total_amount, pay_amount", required = true)
    private List<String> min;

    /** 计算和值的字段 */
    @ApiModelProperty(value = "计算和值的字段", example = "total_amount, pay_amount", required = true)
    private List<String> sum;

    /** 计算平均值的字段 */
    @ApiModelProperty(value = "计算平均值的字段", example = "total_amount, pay_amount", required = true)
    private List<String> avg;

    /** 计算数量的字段 */
    @ApiModelProperty(value = "计算数量的字段", example = "order_code", required = true)
    private String count;
}
