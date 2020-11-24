package com.deloitte.bdh.data.collation.component.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 整理组件字段修改模型
 *
 * @author chenghzhang
 * @date 2020/11/24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArrangeModifyModel {
    /** 字段名称 */
    @ApiModelProperty(value = "字段名称", example = "user_name", required = true)
    private String name;
    /** 字段描述 */
    @ApiModelProperty(value = "字段描述", example = "用户名", required = true)
    private String desc;
    /** 字段类型 */
    @ApiModelProperty(value = "字段类型", example = "Text", required = true)
    private String type;
}
