package com.deloitte.bdh.data.analyse.model.datamodel.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author:LIJUN
 * Date:26/11/2020
 * Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaxMinDto {

    @ApiModelProperty(value = "最大值")
    private Object min;

    @ApiModelProperty(value = "最小值")
    private Object max;

}
