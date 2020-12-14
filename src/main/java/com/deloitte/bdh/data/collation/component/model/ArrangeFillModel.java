package com.deloitte.bdh.data.collation.component.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 整理组件填充模型
 *
 * @author chenghzhang
 * @date 2020/12/14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArrangeFillModel {
    /** 填充字段 */
    @ApiModelProperty(value = "填充字段", example = "user_name", required = true)
    private String name;
    /** 填充值 */
    @ApiModelProperty(value = "填充值", example = "0", required = true)
    private String value;
}
