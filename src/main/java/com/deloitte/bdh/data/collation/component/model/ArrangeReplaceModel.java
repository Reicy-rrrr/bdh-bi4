package com.deloitte.bdh.data.collation.component.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 整理组件替换模型
 *
 * @author chenghzhang
 * @date 2020/11/09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArrangeReplaceModel {
    /** 字段名称 */
    @ApiModelProperty(value = "字段名称", example = "name", required = true)
    private String name;
    /** 原字符串内容 */
    @ApiModelProperty(value = "原字符串内容", example = "zhangsan", required = true)
    private String source;
    /** 替换内容 */
    @ApiModelProperty(value = "替换内容", example = "Zhang San", required = true)
    private String target;
}
