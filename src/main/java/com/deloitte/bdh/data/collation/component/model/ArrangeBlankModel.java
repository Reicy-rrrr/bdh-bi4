package com.deloitte.bdh.data.collation.component.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 整理组件空格处理模型
 *
 * @author chenghzhang
 * @date 2020/11/12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArrangeBlankModel {
    /** 字段名称 */
    @ApiModelProperty(value = "字段名称", example = "user_name", required = true)
    private String name;
    /** 空格处理类型 */
    @ApiModelProperty(value = "原字段值", example = "all, left, right", required = true)
    private String type;
    /** 左右处理时的长度（从左/右起多少字符） */
    @ApiModelProperty(value = "左右处理时的长度（从左/右起多少字符）", example = "10")
    private Integer length;
}
