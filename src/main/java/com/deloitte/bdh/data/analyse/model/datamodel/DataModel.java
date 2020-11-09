package com.deloitte.bdh.data.analyse.model.datamodel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class DataModel extends BaseComponentDataRequestConfig {
    /**
     * x轴相关配置
     */
    @ApiModelProperty(value = "查询的表", notes = "树上所选表")
    String tableName;
    @ApiModelProperty(value = "横向显示的字段", notes = "维度和度量")
    List<DataModelField> x;
    @ApiModelProperty(value = "页开始", notes = "1开始")
    Integer pageIndex;
    @ApiModelProperty(value = "页大小")
    Integer pageSize;
}
