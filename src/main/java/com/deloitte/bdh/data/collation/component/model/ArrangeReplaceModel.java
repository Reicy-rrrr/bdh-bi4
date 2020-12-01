package com.deloitte.bdh.data.collation.component.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 整理组件替换内容模型
 *
 * @author chenghzhang
 * @date 2020/12/01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArrangeReplaceModel {
    /** 字段名称 */
    @ApiModelProperty(value = "字段名称", example = "name", required = true)
    private String name;
    /** 替换内容 */
    @ApiModelProperty(value = "替换内容", example = "zhangsan", required = true)
    private List<ArrangeReplaceContentModel> contents;
}
