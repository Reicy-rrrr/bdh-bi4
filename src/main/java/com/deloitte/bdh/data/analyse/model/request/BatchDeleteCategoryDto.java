package com.deloitte.bdh.data.analyse.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "批量删除目录")
public class BatchDeleteCategoryDto {

    @ApiModelProperty(value = "categoryId")
    List<String> ids;

}
