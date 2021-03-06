package com.deloitte.bdh.data.collation.model.request;


import com.deloitte.bdh.data.collation.component.model.ArrangeCaseConvertModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 整理组件（大小写转换）请求参数
 *
 * @author chenghzhang
 * @date 2020-11-11
 */
@ApiModel(description = "整理组件（大小写转换）请求参数")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArrangeCaseConvertDto extends ArrangeComponentDto {
    @ApiModelProperty(value = "转换字段", example = "")
    @NotNull(message = " 转换字段 不能为空")
    private List<ArrangeCaseConvertModel> fields;
}
