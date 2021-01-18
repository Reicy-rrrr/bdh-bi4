package com.deloitte.bdh.data.collation.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询表数据DTO
 *
 * @author chenghzhang
 * @date 2020/10/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetTableDataDto {
    /** 数据源id */
    @ApiModelProperty(value = "数据源id", example = "101", required = true)
    private String dbId;

    /** 数据表名称 */
    @ApiModelProperty(value = "数据表名称", example = "tb_user", required = true)
    private String tableName;

    /** 当前页码 */
    @ApiModelProperty(value = "当前页码", example = "1", required = true)
    private int page = 1;

    /** 每页记录数 */
    @ApiModelProperty(value = "每页记录数", example = "10", required = true)
    private int size = 10;
}
