package com.deloitte.bdh.data.collation.model.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 修改整理组件（排空）请求参数
 *
 * @author chenghzhang
 * @date 2020-11-09
 */
@ApiModel(description = "修改整理组件（排空）请求参数")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateArrangeNonNullDto extends UpdateArrangeComponentDto {
    @ApiModelProperty(value = "排空字段", example = "")
    private List<String> fields;
}
