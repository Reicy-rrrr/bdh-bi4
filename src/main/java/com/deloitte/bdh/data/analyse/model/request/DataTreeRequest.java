package com.deloitte.bdh.data.analyse.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
public class DataTreeRequest {
    @ApiModelProperty(value = "tenantId", required = true)
    @NotBlank(message = "pageid 不能为空")
    private String pageId;

    @ApiModelProperty(value = "表名", required = true)
    @NotBlank
    private String tableName;
}
