package com.deloitte.bdh.data.analyse.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Author:LIJUN
 * Date:09/11/2020
 * Description:
 */
@Data
public class GetDataTreeRequest implements Serializable {

    @ApiModelProperty(value = "tenantId", required = true)
    @NotBlank(message = "pageid 不能为空")
    private String pageId;

    @ApiModelProperty(value = "数据模型ID", required = true)
    @NotBlank
    private String modelId;
}
