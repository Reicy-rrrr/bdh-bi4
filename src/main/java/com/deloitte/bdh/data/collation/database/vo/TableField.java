package com.deloitte.bdh.data.collation.database.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TableField
 *
 * @author chenghzhang
 * @date 2020/10/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableField {
    /** 字段类型 */
    @ApiModelProperty(value = "字段类型", example = "String", required = true)
    private String type;

    /** 字段名称 */
    @ApiModelProperty(value = "字段名称", example = "name", required = true)
    private String name;

    /** 字段描述 */
    @ApiModelProperty(value = "字段描述", example = "名称", required = true)
    private String desc;
}
