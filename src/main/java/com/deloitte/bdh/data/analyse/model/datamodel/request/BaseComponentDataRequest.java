package com.deloitte.bdh.data.analyse.model.datamodel.request;

import com.deloitte.bdh.data.analyse.model.datamodel.DataConfig;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BaseComponentDataRequest {

    @ApiModelProperty(value = "图表类型")
    String type;

    @ApiModelProperty(value = "图标数据相关配置")
    DataConfig dataConfig;

}
