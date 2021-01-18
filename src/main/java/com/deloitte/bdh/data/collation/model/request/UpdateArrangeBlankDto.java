package com.deloitte.bdh.data.collation.model.request;


import com.deloitte.bdh.data.collation.component.model.ArrangeBlankModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 修改整理组件（去除字段中空格）请求参数
 *
 * @author chenghzhang
 * @date 2020-11-09
 */
@ApiModel(description = "修改整理组件（去除字段中空格）请求参数")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateArrangeBlankDto extends UpdateArrangeComponentDto {
    @ApiModelProperty(value = "去除空格字段", example = "")
    @NotNull(message = " 去除空格字段 不能为空")
    private List<ArrangeBlankModel> fields;
}
