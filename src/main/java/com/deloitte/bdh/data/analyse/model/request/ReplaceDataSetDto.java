package com.deloitte.bdh.data.analyse.model.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel(description = "数据集修改表名称")
@Data
public class ReplaceDataSetDto {

    @ApiModelProperty(value = "报表id", required = true)
    @NotNull(message = "报表id")
    private String pageId;

    @ApiModelProperty(value = "数据集替换集合", required = true)
    @NotNull(message = "数据集替换集合")
    private List<ReplaceItemDto> replaceItemDtoList;
}
