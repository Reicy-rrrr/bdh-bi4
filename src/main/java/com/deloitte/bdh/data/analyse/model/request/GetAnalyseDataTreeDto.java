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
public class GetAnalyseDataTreeDto implements Serializable {

    @ApiModelProperty(value = "Y：来自数据集；N：来自报表", required = true)
    @NotBlank
    private String isDataResource;

    @ApiModelProperty(value = "数据模型ID", required = true)
    @NotBlank
    private String modelId;
}
