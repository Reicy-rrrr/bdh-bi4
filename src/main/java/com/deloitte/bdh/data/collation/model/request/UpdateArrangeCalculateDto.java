package com.deloitte.bdh.data.collation.model.request;


import com.deloitte.bdh.data.collation.component.model.ArrangeCalculateModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 修改整理组件（计算字段）请求参数
 *
 * @author chenghzhang
 * @date 2020-12-17
 */
@ApiModel(description = "修改整理组件（计算字段）请求参数")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateArrangeCalculateDto extends UpdateArrangeComponentDto {
    @ApiModelProperty(value = "计算字段", example = "")
    @NotNull(message = " 计算字段 不能为空")
    private ArrangeCalculateModel fields;
}
