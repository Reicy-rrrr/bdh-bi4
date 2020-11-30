package com.deloitte.bdh.data.analyse.model.datamodel;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

@Data
public class DataModel {

    @NotBlank
    @ApiModelProperty(value = "查询的表", notes = "树上所选表")
    String tableName;

    @ApiModelProperty(value = "横向显示的字段", notes = "横向显示的字段")
    List<DataModelField> x = Lists.newArrayList();

    @ApiModelProperty(value = "纵向显示的字段", notes = "纵向显示的字段")
    List<DataModelField> y = Lists.newArrayList();

    @ApiModelProperty(value = "双Y轴纵向显示的字段", notes = "双Y轴纵向显示的字段")
    List<DataModelField> y2 = Lists.newArrayList();

    @ApiModelProperty(value = "图例", notes = "图例")
    List<DataModelField> category = Lists.newArrayList();

    @ApiModelProperty(value = "页开始", notes = "1开始")
    Integer page;

    @ApiModelProperty(value = "页大小")
    Integer pageSize = 10;

    @ApiModelProperty(value = "自定义参数")
    Map<String, Object> customParams;

    @ApiModelProperty(value = "条件")
    List<DataCondition> conditions;
}
