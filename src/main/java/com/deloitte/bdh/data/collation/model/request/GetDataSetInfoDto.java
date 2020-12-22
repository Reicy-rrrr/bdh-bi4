package com.deloitte.bdh.data.collation.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetDataSetInfoDto extends PageDto {
    /**
     * 数据表名称
     */
    @ApiModelProperty(value = "数据表别名", example = "tb_user", required = true)
    @NotNull(message = "数据表别名 不能为空")
    private String tableDesc;

}
