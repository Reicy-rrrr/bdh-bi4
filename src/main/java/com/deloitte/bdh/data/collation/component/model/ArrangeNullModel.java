package com.deloitte.bdh.data.collation.component.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 整理组件空值模型
 *
 * @author chenghzhang
 * @date 2020/12/15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArrangeNullModel {
    /** 排除空值字段集合 */
    @ApiModelProperty(value = "排除空值字段集合", example = "", required = true)
    private List<String> nonNullFields;
    /** 空值填充字段集合 */
    @ApiModelProperty(value = "空值填充字段集合", example = "name", required = true)
    private List<ArrangeFillModel> fillNullFields;
}
