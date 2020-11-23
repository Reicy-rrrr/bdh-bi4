package com.deloitte.bdh.data.collation.component.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 整理组件合并模型
 *
 * @author chenghzhang
 * @date 2020/11/23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArrangeCombineModel {
    /** 合并字段：左侧字段 */
    @ApiModelProperty(value = "合并字段：左侧字段", example = "user_name", required = true)
    private String left;
    /** 合并字段：右侧字段 */
    @ApiModelProperty(value = "合并字段：左侧字段", example = "country", required = true)
    private String right;
    /** 合并字段连接符 */
    @ApiModelProperty(value = "合并字段连接符", example = "-", required = true)
    private String connector;
}
