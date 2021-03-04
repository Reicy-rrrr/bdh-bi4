package com.deloitte.bdh.data.collation.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;



@ApiModel(description = "基于租户获取下载请求参数")
@Setter
@Getter
@ToString
public class GetDownloadPageDto extends PageDto {

    @ApiModelProperty(value = "状态", example = "1")
    private String status;

    @ApiModelProperty(value = "1")
    private String superUserFlag;
}
