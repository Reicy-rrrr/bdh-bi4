package com.deloitte.bdh.data.collation.model.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 运算符对象
 *
 * @author chenghzhang
 * @date 2020-12-25
 */
@ApiModel(description = "运算符对象")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculateOperatorResp {
    /**
     * 公式
     **/
    @ApiModelProperty(value = "公式", example = "+")
    private String operator;
    /**
     * 公式名称
     **/
    @ApiModelProperty(value = "公式名称", example = "加号 +")
    private String name;
    /**
     * 公式描述
     **/
    @ApiModelProperty(value = "公式描述", example = "数值内容相减")
    private String desc;
    /**
     * 公式示例
     **/
    @ApiModelProperty(value = "公式示例", example = "2+3=5")
    private List<String> examples;
}
