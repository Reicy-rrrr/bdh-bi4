package com.deloitte.bdh.data.collation.component.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 连接字段模型
 *
 * @author chenghzhang
 * @date 2020/10/27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoinFieldModel {
    /** 左边表字段 */
    @ApiModelProperty(value = "左边表字段", example = "product_id")
    private String leftField;

    /** 右边表字段 */
    @ApiModelProperty(value = "右边表字段", example = "id")
    private String rightField;
}
