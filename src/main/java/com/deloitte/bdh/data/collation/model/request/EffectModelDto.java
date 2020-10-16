package com.deloitte.bdh.data.collation.model.request;

import com.deloitte.bdh.data.collation.enums.EffectEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@ApiModel(description = "启用/停用 MODEL")
@Setter
@Getter
@ToString
public class EffectModelDto {
    @ApiModelProperty(value = "模板id", example = "123", required = true)
    @NotNull(message = "模板id 不能为空")
    private String id;

    @ApiModelProperty(value = "启用/禁用", example = "ENABLE/DISABLE", required = true)
    @NotNull(message = "状态 不能为空")
    private String effect = EffectEnum.ENABLE.getKey();

    @ApiModelProperty(value = "modifiedUser", example = "0", required = true)
    @NotNull(message = "modifiedUser 不能为空")
    private String modifiedUser;
}
