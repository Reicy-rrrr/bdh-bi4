package com.deloitte.bdh.data.collation.model.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 整理组件（去空格）请求参数
 *
 * @author chenghzhang
 * @date 2020-11-11
 */
@ApiModel(description = "整理组件（去空格）请求参数")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArrangeTrimDto extends ArrangeComponentDto {
    @ApiModelProperty(value = "去空格字段", example = "")
    private List<String> fields;
}
