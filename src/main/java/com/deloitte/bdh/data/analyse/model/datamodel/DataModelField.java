package com.deloitte.bdh.data.analyse.model.datamodel;

import com.deloitte.bdh.data.analyse.enums.AggregateTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DataModelField {

    @ApiModelProperty(value = "前端对应的key")
    String frontendId;

    @ApiModelProperty(value = "数据库对应的cloumn,如果有抽象后对应的抽象的列id")
    String id;

    @ApiModelProperty(value = "维度为WD,度量为DL")
    String type;

    @ApiModelProperty(value = "数据类型")
    String dataType;

    @ApiModelProperty(value = "别名")
    String alias;

    @ApiModelProperty("维度为WD,度量为DL")
    private String quota;

    @ApiModelProperty("聚合方式")
    private String aggregateType = AggregateTypeEnum.SUM.getKey();

    @ApiModelProperty("排序方式")
    private String orderType;
}
