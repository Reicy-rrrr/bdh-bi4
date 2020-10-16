package com.deloitte.bdh.data.collation.model.request;

import com.deloitte.bdh.data.collation.enums.RunStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@ApiModel(description = "运行/停止 model")
@Setter
@Getter
@ToString
public class RunModelDto {
    @ApiModelProperty(value = "模板id", example = "123", required = true)
    @NotNull(message = "模板id 不能为空")
    private String id;

    @ApiModelProperty(value = "运行/停止", example = "RUNNING/STOP", required = true)
    @NotNull(message = "状态 不能为空")
    private String runStatus = RunStatusEnum.RUNNING.getKey();

    @ApiModelProperty(value = "modifiedUser", example = "0", required = true)
    @NotNull(message = "modifiedUser 不能为空")
    private String modifiedUser;
}
