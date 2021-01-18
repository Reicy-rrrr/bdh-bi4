package com.deloitte.bdh.data.collation.component.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 整理结果模型
 *
 * @author chenghzhang
 * @date 2020/11/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArrangeResultModel {
    /** 整理后的字段 */
    @ApiModelProperty(value = "整理后的字段", example = "", required = true)
    private String field;
    /** 整理后的sql片段 */
    @ApiModelProperty(value = "整理后的sql片段", example = "", required = true)
    private String segment;
    /** 是否为新字段 */
    @ApiModelProperty(value = "是否为新字段", example = "", required = true)
    private Boolean isNew;
    /** 字段映射 */
    @ApiModelProperty(value = "字段映射", example = "", required = true)
    private FieldMappingModel mapping;
}
