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
public class ArrangeGroupEnumModel {
    /** 字段名称 */
    @ApiModelProperty(value = "字段名称", example = "type", required = true)
    private String name;
    /** 分组内容 */
    @ApiModelProperty(value = "分组内容", example = "", required = true)
    private List<ArrangeGroupEnumFieldModel> groups;
    /** 分组后其他内容 */
    @ApiModelProperty(value = "分组后其他内容", example = "其他/other", required = true)
    private String other = "other";
}
