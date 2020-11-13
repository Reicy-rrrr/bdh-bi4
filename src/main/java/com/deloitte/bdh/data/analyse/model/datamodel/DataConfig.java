package com.deloitte.bdh.data.analyse.model.datamodel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
public class DataConfig {

    @ApiModelProperty(value = "是否自动刷新")
    Boolean needAutoRefresh = false;

    @NotBlank
    @ApiModelProperty(value = "表格子类型")
    String tableType;

    @ApiModelProperty(value = "是否聚合")
    Boolean tableNotAggregate = false;

    @NotNull
    @ApiModelProperty(value = "数据模型相关配置")
    DataModel dataModel;
}
