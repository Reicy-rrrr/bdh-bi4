package com.deloitte.bdh.data.collation.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class PageDto {
    @ApiModelProperty(value = "分页参数，页码,默认 0", example = "0")
    private Integer page = 1;
    @ApiModelProperty(value = "分页参数，每页数量,默认 10", example = "10")
    private Integer size = 10;
}
