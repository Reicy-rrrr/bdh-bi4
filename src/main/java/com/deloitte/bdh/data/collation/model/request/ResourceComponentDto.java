package com.deloitte.bdh.data.collation.model.request;


import com.deloitte.bdh.common.util.NifiProcessUtil;
import com.deloitte.bdh.data.collation.database.po.TableField;
import com.deloitte.bdh.data.collation.enums.ComponentTypeEnum;
import com.deloitte.bdh.data.collation.enums.SyncTypeEnum;
import com.deloitte.bdh.data.collation.enums.YesOrNoEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel(description = "引入数据源 请求参数")
@Setter
@Getter
@ToString
public class ResourceComponentDto {

    @ApiModelProperty(value = "modelId", example = "0", required = true)
    @NotNull(message = " 模板id 不能为空")
    private String modelId;

    @ApiModelProperty(value = "componentName", example = "0", required = true)
    private String componentName = ComponentTypeEnum.DATASOURCE.getValue() + System.currentTimeMillis();

    @ApiModelProperty(value = "sourceId", example = "0", required = true)
    @NotNull(message = "数据源id 不能为空")
    private String sourceId;

    @ApiModelProperty(value = "tableName", example = "0")
    private String tableName;

    @ApiModelProperty(value = "是否独立副本", example = "0")
    private String duplicate = YesOrNoEnum.YES.getKey();

    @ApiModelProperty(value = "不是独立副本时，所属的编码", example = "0")
    private String belongMappingCode;

    @ApiModelProperty(value = "同步方式", example = "0：直连，1：全量，2：增量")
    private Integer syncType = SyncTypeEnum.FULL.getKey();

    @ApiModelProperty(value = "偏移字段", example = "0")
    @NotNull(message = "偏移字段 不能为空")
    private String offsetField;

    @ApiModelProperty(value = "偏移量（第一次设置代表第一次同步的开始位置）", example = "0")
    private String offsetValue;

    @ApiModelProperty(value = "字段列表", example = "0")
    private List<TableField> fields;

    @ApiModelProperty(value = "坐标", example = "1")
    private String position = NifiProcessUtil.randPosition();
}
