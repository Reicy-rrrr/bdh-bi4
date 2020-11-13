package com.deloitte.bdh.data.analyse.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * Author:LIJUN
 * Date:12/11/2020
 * Description:
 */
@Data
public class AnalyseNameDto implements Serializable {

    @ApiModelProperty(value = "name")
    String name;

}
