package com.deloitte.bdh.data.collation.component.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 整理组件分组字段模型(列举类型)
 *
 * @author chenghzhang
 * @date 2020/11/11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArrangeGroupEnumFieldModel {
    /** 原字段值 */
    @ApiModelProperty(value = "原字段值", example = "integer, long, double", required = true)
    private List<String> sources;
    /** 分组后字段值 */
    @ApiModelProperty(value = "分组后字段值", example = "number", required = true)
    private String target;
}
