package com.deloitte.bdh.data.collation.database.po;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TableInfo
 *
 * @author chenghzhang
 * @date 2020/12/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableInfo {
    /** 表名称 */
    @ApiModelProperty(value = "表名称", example = "tb_order", required = true)
    private String toTableName;

    /** 表描述 */
    @ApiModelProperty(value = "表描述", example = "订单表", required = true)
    private String toTableDesc;
}
