package com.deloitte.bdh.data.collation.component.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 连接模型
 *
 * @author chenghzhang
 * @date 2020/10/27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoinModel {
    /** 表名 */
    @ApiModelProperty(value = "tableName", example = "tb_product", required = true)
    private String tableName;

    /** 关联类型：left，inner，full */
    @ApiModelProperty(value = "关联类型", example = "left，inner，full", required = true)
    private String joinType;

    /** 左边表名 */
    @ApiModelProperty(value = "左边表名", example = "tb_order", required = true)
    private String leftTableName;

    /** 关联的字段 */
    @ApiModelProperty(value = "关联的字段", example = "leftField: id, rightField: order_id", required = true)
    private List<JoinFieldModel> joinFields;

    /** 右边的表 */
    @JsonIgnore
    private List<JoinModel> right;
}
