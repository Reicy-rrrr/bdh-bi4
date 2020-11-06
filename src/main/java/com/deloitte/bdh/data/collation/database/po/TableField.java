package com.deloitte.bdh.data.collation.database.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class TableField implements Cloneable {
    /** 字段类型 */
    @ApiModelProperty(value = "字段类型", example = "String", required = true)
    private String type;

    /** 字段名称 */
    @ApiModelProperty(value = "字段名称", example = "name", required = true)
    private String name;

    /** 字段描述 */
    @ApiModelProperty(value = "字段描述", example = "名称", required = true)
    private String desc;

    /** 字段列类型 */
    @ApiModelProperty(value = "字段列类型", example = "decimal(10,4)")
    private String columnType;

    /** 字段数据类型 */

    @ApiModelProperty(value = "字段数据类型", example = "decimal")
    private String dataType;

    /** 字段数据范围 */
    @ApiModelProperty(value = "字段数据范围", example = "10,4")
    private String dataScope;

    @Override
    public TableField clone() {
        try {
            return (TableField) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
