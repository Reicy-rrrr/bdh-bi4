package com.deloitte.bdh.data.collation.model.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 整理组件（删除字段）请求参数
 *
 * @author chenghzhang
 * @date 2020-11-09
 */
@ApiModel(description = "整理组件（删除字段）请求参数")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArrangeRemoveDto extends ArrangeComponentDto {
    @ApiModelProperty(value = "删除字段", example = "")
    private List<String> fields;
}
