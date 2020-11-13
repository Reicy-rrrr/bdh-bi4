package com.deloitte.bdh.data.analyse.model.datamodel.request;

import com.deloitte.bdh.data.analyse.model.datamodel.DataConfig;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
public class BaseComponentDataRequest {

    @NotBlank
    @ApiModelProperty(value = "图表类型")
    String type;

    @NotNull
    @ApiModelProperty(value = "图标数据相关配置")
    DataConfig dataConfig;

}
