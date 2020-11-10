package com.deloitte.bdh.data.collation.component.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 整理组件：转小写模型
 *
 * @author chenghzhang
 * @date 2020/11/09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArrangeLowerModel {
    /** 转小写字段名称集合 */
    @ApiModelProperty(value = "转小写字段名称集合", example = "name", required = true)
    private List<String> fields;
}
