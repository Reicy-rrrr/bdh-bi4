package com.deloitte.bdh.data.analyse.model.datamodel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class DataModelField {

    @ApiModelProperty(value = "前端对应的key")
    String frontendId;

    @NotBlank
    @ApiModelProperty(value = "数据库对应的cloumn,如果有抽象后对应的抽象的列id")
    String id;

    @ApiModelProperty("type")
    String type;

    @NotBlank
    @ApiModelProperty(value = "数据类型")
    String dataType;

    @ApiModelProperty(value = "别名")
    String alias;

    @NotBlank
    @ApiModelProperty("维度为WD,度量为DL")
    private String quota;

    @ApiModelProperty("聚合方式")
    private String aggregateType;

    @ApiModelProperty("排序方式")
    private String orderType;

    @ApiModelProperty("symbol")
    private String symbol;

    @ApiModelProperty("value")
    private String value;

    @ApiModelProperty("转换类型")
    private String formatType;

    @ApiModelProperty("精度")
    private Integer precision;

    @ApiModelProperty("数据单位")
    private String dataUnit;

    @ApiModelProperty("默认值")
    private String defaultValue;

    @ApiModelProperty("是否需要group")
    private boolean needGroup = false;

    @ApiModelProperty("对比值")
    private Object contrastValue;
}
