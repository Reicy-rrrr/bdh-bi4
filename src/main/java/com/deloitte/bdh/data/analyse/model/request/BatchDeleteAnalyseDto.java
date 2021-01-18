package com.deloitte.bdh.data.analyse.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

@Data
@ApiModel(description = "批量删除")
public class BatchDeleteAnalyseDto {

    @NotEmpty
    @ApiModelProperty(value = "id")
    List<String> ids;

    @NotEmpty
    @ApiModelProperty(value = "type")
    String type;

}
