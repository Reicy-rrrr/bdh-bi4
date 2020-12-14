package com.deloitte.bdh.data.collation.model.request;


import com.deloitte.bdh.data.collation.component.model.ArrangeFillModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 修改整理组件（填充字段）请求参数
 *
 * @author chenghzhang
 * @date 2020-12-14
 */
@ApiModel(description = "修改整理组件（填充字段）请求参数")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateArrangeFillDto extends UpdateArrangeComponentDto {
    @ApiModelProperty(value = "填充字段", example = "")
    @NotNull(message = " 填充字段 不能为空")
    private List<ArrangeFillModel> fields;
}
