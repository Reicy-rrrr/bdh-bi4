package com.deloitte.bdh.data.collation.model.request;


import com.deloitte.bdh.data.collation.database.po.TableField;
import com.deloitte.bdh.data.collation.enums.SyncTypeEnum;
import com.deloitte.bdh.data.collation.enums.YesOrNoEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel(description = "输出组件 请求参数")
@Setter
@Getter
@ToString
public class OutComponentDto extends BaseRequest {

    @ApiModelProperty(value = "modelId", example = "0", required = true)
    @NotNull(message = " 模板id 不能为空")
    private String modelId;

    @ApiModelProperty(value = "tableName", example = "0")
    private String tableName;

    @ApiModelProperty(value = "字段列表", example = "0")
    @NotNull(message = " 字段列表 不能为空")
    private List<TableField> fields;

    @ApiModelProperty(value = "sqlSelectQuery", example = "0")
    @NotNull(message = " sqlSelectQuery 不能为空")
    private String sqlSelectQuery;
}
