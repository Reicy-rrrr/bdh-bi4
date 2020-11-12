package com.deloitte.bdh.data.analyse.model.request;

import com.deloitte.bdh.data.collation.model.request.PageDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "文件夹/报表管理")
public class GetAnalysePageDto extends PageDto {

    @ApiModelProperty(value = "categoryId")
    @NotNull(message = "目录id")
    private String categoryId;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "已发布,所有,PUBLISH,EDIT")
    private String type;
}
