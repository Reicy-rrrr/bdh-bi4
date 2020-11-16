package com.deloitte.bdh.data.analyse.model.datamodel;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class DataModel {

    @ApiModelProperty(value = "查询的表", notes = "树上所选表")
    String tableName;

    @ApiModelProperty(value = "横向显示的字段", notes = "横向显示的字段")
    List<DataModelField> x = Lists.newArrayList();

    @ApiModelProperty(value = "纵向显示的字段", notes = "纵向显示的字段")
    List<DataModelField> y = Lists.newArrayList();

    @ApiModelProperty(value = "页开始", notes = "1开始")
    Integer page;

    @ApiModelProperty(value = "页大小")
    Integer pageSize;
}
