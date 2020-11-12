package com.deloitte.bdh.data.analyse.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "批量删除PAGE")
public class BatchDelAnalysePageDto {

    @ApiModelProperty(value = "pageIds")
    List<String> ids;
}
