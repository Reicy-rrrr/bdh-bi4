package com.deloitte.bdh.data.analyse.model.datamodel.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class BaseComponentDataResponse {

    @ApiModelProperty(value = "列名称")
    Map<String, List<ListTree>> columns;

    @ApiModelProperty(value = "数据")
    List<Map<String, Object>> rows;

    private String sql;
}
