package com.deloitte.bdh.data.collation.component.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 整理组件分组模型
 *
 * @author chenghzhang
 * @date 2020/11/09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArrangeGroupModel {
    /** 字段名称 */
    @ApiModelProperty(value = "字段名称", example = "type", required = true)
    private String field;
    /** 分组属性 */
    @ApiModelProperty(value = "分组属性", example = "", required = true)
    private List<ArrangeGroupFieldModel> groups;
}
