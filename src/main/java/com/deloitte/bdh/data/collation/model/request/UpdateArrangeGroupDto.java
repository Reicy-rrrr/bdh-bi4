package com.deloitte.bdh.data.collation.model.request;


import com.deloitte.bdh.data.collation.component.model.ArrangeGroupModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 修改整理组件（分组）请求参数
 *
 * @author chenghzhang
 * @date 2020-11-09
 */
@ApiModel(description = "修改整理组件（分组））请求参数")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateArrangeGroupDto extends UpdateArrangeComponentDto {
    @ApiModelProperty(value = "分组字段", example = "")
    @NotNull(message = " 分组字段 不能为空")
    private ArrangeGroupModel fields;
}
