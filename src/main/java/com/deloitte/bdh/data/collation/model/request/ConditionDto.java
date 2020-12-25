package com.deloitte.bdh.data.collation.model.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import java.util.List;


@ApiModel(description = "数据源过滤条件参数")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConditionDto {
    @NotBlank
    @ApiModelProperty("field")
    private String field;

    @NotBlank
    @ApiModelProperty("symbol")
    private String symbol;

    @NotBlank
    @ApiModelProperty("value")
    private List<String> values;
}
