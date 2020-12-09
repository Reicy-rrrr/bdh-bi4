package com.deloitte.bdh.data.collation.model.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@ApiModel(description = "数据集")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataSetResp implements Serializable {

    @ApiModelProperty(value = "模型编码")
    private String modelCode;

    @ApiModelProperty(value = "模型名称")
    private String modelName;

    @ApiModelProperty(value = "表名称")
    private String tableName;

    @ApiModelProperty(value = "描述")
    private String comments;

    @ApiModelProperty(value = "上次刷新时间")
    private String lastExecuteDate;


}
