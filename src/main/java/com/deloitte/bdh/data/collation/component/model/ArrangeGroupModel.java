package com.deloitte.bdh.data.collation.component.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 整理组件分组模型
 *
 * @author chenghzhang
 * @date 2020/11/09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArrangeGroupModel {
    /** 区间类型字段 */
    @ApiModelProperty(value = "区间类型字段", example = "", required = true)
    private List<ArrangeGroupSectModel> sectFields;
    /** 列举类型字段 */
    @ApiModelProperty(value = "列举类型字段", example = "", required = true)
    private List<ArrangeGroupEnumModel> enumFields;
}
