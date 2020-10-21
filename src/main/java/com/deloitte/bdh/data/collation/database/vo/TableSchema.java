package com.deloitte.bdh.data.collation.database.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * TableSchema
 *
 * @author chenghzhang
 * @date 2020/10/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableSchema {
    /** 标题 */
    @ApiModelProperty(value = "标题", example = "mysql")
    private String title;

    /** 类型 */
    @ApiModelProperty(value = "类型", example = "crud")
    private String type;

    /** 字段列表 */
    @ApiModelProperty(value = "字段列表", example = "[]", required = true)
    private List<TableField> columns;
}
