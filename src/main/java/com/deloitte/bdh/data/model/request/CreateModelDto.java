package com.deloitte.bdh.data.model.request;

import com.deloitte.bdh.common.util.NifiProcessUtil;
import com.deloitte.bdh.data.enums.YesOrNoEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;


@ApiModel(description = "新增Model")
@Setter
@Getter
@ToString
public class CreateModelDto {

    @ApiModelProperty(value = "tenantId", example = "123", required = true)
    @NotNull(message = "租户id 不能为空")
    private String tenantId;

    @ApiModelProperty(value = "Model名称(ETL名称)", example = "Model 名称", required = true)
    @NotNull(message = "ETL名称 不能为空")
    private String name;

    @ApiModelProperty(value = "描述", example = "描述")
    private String comments;

    @ApiModelProperty(value = "是否是文件夹（0/1）", example = "0")
    @NotNull(message = "是否是文件夹 不能为空")
    private String isFile = YesOrNoEnum.NO.getKey();

    @ApiModelProperty(value = "坐标Json", example = "0")
    private String position = NifiProcessUtil.randPosition();

    @ApiModelProperty(value = "父编码", example = "xxxxx")
    @NotNull(message = "父编码 不能为空")
    private String parentCode = "0";

    @ApiModelProperty(value = "createUser", example = "1", required = true)
    @NotNull(message = "createUser 不能为空")
    private String createUser;

}
