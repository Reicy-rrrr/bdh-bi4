package com.deloitte.bdh.data.collation.model.request;


import com.deloitte.bdh.data.collation.database.po.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel(description = "修改数据源 请求参数")
@Setter
@Getter
@ToString
public class UpdateResourceComponentDto {

    @ApiModelProperty(value = "componentCode", example = "0", required = true)
    @NotNull(message = " 组件编码 不能为空")
    private String componentCode;

    @ApiModelProperty(value = "componentName", example = "0", required = true)
    @NotNull(message = " 组件名称 不能为空")
    private String componentName ;

    @ApiModelProperty(value = "sourceId", example = "0", required = true)
    @NotNull(message = "数据源id 不能为空")
    private String sourceId;

    @ApiModelProperty(value = "tableName", example = "0")
    private String tableName;

    @ApiModelProperty(value = "是否独立副本", example = "0")
    private String duplicate;

    @ApiModelProperty(value = "不是独立副本时，所属的编码", example = "0")
    private String belongMappingCode;

    @ApiModelProperty(value = "同步方式", example = "0：直连，1：全量，2：增量")
    private Integer syncType ;

    @ApiModelProperty(value = "偏移字段", example = "0")
    private String offsetField;

    @ApiModelProperty(value = "偏移量（第一次设置代表第一次同步的开始位置）", example = "0")
    private String offsetValue;

    @ApiModelProperty(value = "字段列表", example = "0")
    private List<TableField> fields;

}
