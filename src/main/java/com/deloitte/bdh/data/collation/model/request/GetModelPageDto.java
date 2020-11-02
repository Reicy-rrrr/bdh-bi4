package com.deloitte.bdh.data.collation.model.request;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@ApiModel(description = "基于租户获取Model列表请求参数")
@Setter
@Getter
@ToString
public class GetModelPageDto extends PageDto {

}
