package com.deloitte.bdh.data.analyse.model.datamodel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * Author:LIJUN
 * Date:18/11/2020
 * Description:
 */
@Data
public class DataCondition {

    @NotEmpty
    @ApiModelProperty("id")
    private List<String> id;

    @NotBlank
    @ApiModelProperty("quota")
    private String quota;

    @ApiModelProperty("formatType")
    private String formatType;

    @ApiModelProperty("聚合方式")
    private String aggregateType;

    @NotBlank
    @ApiModelProperty("symbol")
    private String symbol;

    @NotBlank
    @ApiModelProperty("value")
    private List<String> value;

}
