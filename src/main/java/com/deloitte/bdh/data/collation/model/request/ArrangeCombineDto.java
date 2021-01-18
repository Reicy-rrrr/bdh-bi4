package com.deloitte.bdh.data.collation.model.request;


import com.deloitte.bdh.data.collation.component.model.ArrangeCombineModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 整理组件（合并字段）请求参数
 *
 * @author chenghzhang
 * @date 2020-11-09
 */
@ApiModel(description = "整理组件（合并字段）请求参数")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArrangeCombineDto extends ArrangeComponentDto {
    @ApiModelProperty(value = "合并字段", example = "")
    private List<ArrangeCombineModel> fields;
}
