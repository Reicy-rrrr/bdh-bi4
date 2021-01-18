package com.deloitte.bdh.data.collation.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询表结构DTO
 *
 * @author chenghzhang
 * @date 2020/10/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetTableSchemaDto {
    /** 总记录数 */
    @ApiModelProperty(value = "总记录数", example = "10001", required = true)
    private String dbId;

    /** 总记录数 */
    @ApiModelProperty(value = "总记录数", example = "10001", required = true)
    private String tableName;
}
