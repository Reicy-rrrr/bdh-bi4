package com.deloitte.bdh.data.collation.component.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 整理组件分组字段模型（区间类型：1-1000）
 *
 * @author chenghzhang
 * @date 2020/11/11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArrangeGroupSectFieldModel {
    /** 最小值 */
    @ApiModelProperty(value = "最小值", example = "1", required = true)
    private String minSource;
    /** 最大值 */
    @ApiModelProperty(value = "最大值", example = "1000", required = true)
    private String maxSource;
    /** 分组后字段值 */
    @ApiModelProperty(value = "分组后字段值", example = "number", required = true)
    private String target;
}
