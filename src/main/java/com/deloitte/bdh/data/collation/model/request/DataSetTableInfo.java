package com.deloitte.bdh.data.collation.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataSetTableInfo {
    /** id */
    @ApiModelProperty(value = "id", example = "tb_order", required = true)
    private String id;

    /** code */
    @ApiModelProperty(value = "code", example = "tb_order", required = true)
    private String code;

    /** 表名称 */
    @ApiModelProperty(value = "表名称", example = "tb_order", required = true)
    private String toTableName;

    /** 表描述 */
    @ApiModelProperty(value = "表描述", example = "订单表", required = true)
    private String toTableDesc;
}
