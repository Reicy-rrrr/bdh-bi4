package com.deloitte.bdh.data.collation.database.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author chenghzhang
 */
@Data
public class CreateTableDto {
    @ApiModelProperty(value = "数据源id", example = "123", required = true)
    @NotNull(message = "数据源id不能为空")
    private String dbId;
    @ApiModelProperty(value = "源表名", example = "tb_user", required = true)
    @NotNull(message = "源表名不能为空")
    private String sourceTableName;
    @ApiModelProperty(value = "目标表名", example = "tb_test", required = true)
    @NotNull(message = "目标表名不能为空")
    private String targetTableName;
    @ApiModelProperty(value = "字段", example = "id, user_name, email", required = true)
    private List<String> fields;
}
