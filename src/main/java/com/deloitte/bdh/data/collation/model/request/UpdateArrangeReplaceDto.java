package com.deloitte.bdh.data.collation.model.request;


import com.deloitte.bdh.data.collation.component.model.ArrangeReplaceModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 修改整理组件（替换）请求参数
 *
 * @author chenghzhang
 * @date 2020-11-09
 */
@ApiModel(description = "修改整理组件（替换）请求参数")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateArrangeReplaceDto extends UpdateArrangeComponentDto {
    @ApiModelProperty(value = "替换内容", example = "")
    @NotNull(message = " 替换内容 不能为空")
    private List<ArrangeReplaceModel> fields;
}
