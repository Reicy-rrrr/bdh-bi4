package com.deloitte.bdh.data.collation.model.request;

import com.deloitte.bdh.data.collation.enums.EffectEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@ApiModel(description = "基于租户获取数据源列表请求参数")
@Setter
@Getter
@ToString
public class GetResourcesDto extends PageDto {

    @ApiModelProperty(value = "数据源名称", example = "00001")
    private String name;

    @ApiModelProperty(value = "数据源状态", example = "0/1")
    private String effect;

    @ApiModelProperty(value = "用户标签", example = "1")
    private String superUserFlag;
}
