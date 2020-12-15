package com.deloitte.bdh.data.collation.model.request;


import com.deloitte.bdh.data.collation.component.model.ArrangeNullModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 整理组件（空值）请求参数
 *
 * @author chenghzhang
 * @date 2020-11-09
 */
@ApiModel(description = "整理组件（空值）请求参数")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArrangeNullDto extends ArrangeComponentDto {
    @ApiModelProperty(value = "空值处理字段", example = "")
    private ArrangeNullModel fields;
}
