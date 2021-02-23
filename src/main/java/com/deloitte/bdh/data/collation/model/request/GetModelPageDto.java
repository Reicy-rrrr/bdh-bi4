package com.deloitte.bdh.data.collation.model.request;

import com.deloitte.bdh.data.collation.enums.YesOrNoEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;


@ApiModel(description = "基于租户获取Model列表请求参数")
@Setter
@Getter
@ToString
public class GetModelPageDto extends PageDto {

    @ApiModelProperty(value = "文件夹编码", example = "123", required = true)
    @NotNull(message = "文件夹编码 不能为空")
    private String fileCode;

    @ApiModelProperty(value = "用户标签", example = "1")
    private String superUserFlag = YesOrNoEnum.NO.getKey();
}
