package com.deloitte.bdh.data.analyse.model.datamodel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Author:LIJUN
 * Date:18/11/2020
 * Description:
 */
@Data
public class DataCondition {

    @ApiModelProperty("id")
    private List<String> id;

    @ApiModelProperty("quota")
    private String quota;

    @ApiModelProperty("formatType")
    private String formatType;

    @ApiModelProperty("symbol")
    private String symbol;

    @ApiModelProperty("value")
    private String value;

}
