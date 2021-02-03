package com.deloitte.bdh.data.collation.model.request;

import com.deloitte.bdh.data.collation.model.resp.DataSetTree;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataSetTableInfo {

    /** title */
    @ApiModelProperty(value = "id", example = "tb_order", required = true)
    private String title;

    /** value */
    @ApiModelProperty(value = "id", example = "tb_order", required = true)
    private String value;

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

    /** 表描述 */
    @ApiModelProperty(value = "是否文件夹", example = "1", required = true)
    private String isFile;

    @ApiModelProperty(value = "下级")
    private List<DataSetTableInfo> children;
}
