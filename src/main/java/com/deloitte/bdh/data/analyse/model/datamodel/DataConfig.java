package com.deloitte.bdh.data.analyse.model.datamodel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DataConfig {
    String continuity_type = "simple";
    String continuity_time_step = "1Hour";
    String continuity_empty_val = "0";
    String rateStdDate = "auto";
    Boolean needAutoRefresh = false;
    String tableType = "normal";

    @ApiModelProperty(value = "数据模型相关配置")
    DataModel dataModel;

    @ApiModelProperty(value = "是否聚合", notes = "true,sql需要根据维度groupby 得到度量的合计值")
    Boolean tableNotAggregate = false;
}
