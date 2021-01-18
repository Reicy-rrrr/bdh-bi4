package com.deloitte.bdh.data.collation.component.model;

import com.deloitte.bdh.data.collation.enums.ComponentTypeEnum;
import com.deloitte.bdh.data.collation.model.BiComponentParams;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 组件处理模型
 *
 * @author chenghzhang
 * @date 2020/10/27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComponentModel {

    /** 组件id */
    @ApiModelProperty(value = "ID", example = "10")
    @JsonIgnore
    private String id;

    /** 组件名称 */
    @ApiModelProperty(value = "组件名称", example = "整理1")
    @JsonIgnore
    private String name;

    /** 组件描述 */
    @ApiModelProperty(value = "组件描述", example = "10")
    @JsonIgnore
    private String comments;

    /** 组件code */
    @ApiModelProperty(value = "组件code", required = true)
    @JsonIgnore
    private String code;

    /** 组件类型 */
    @ApiModelProperty(value = "组件类型", example = "10")
    @JsonIgnore
    private String type;

    /** 所属模板code */
    @ApiModelProperty(value = "所属模板code", example = "10")
    @JsonIgnore
    private String refModelCode;

    /** 关联映射code（源组件） */
    @ApiModelProperty(value = "关联映射code", example = "10")
    @JsonIgnore
    private String refMappingCode;

    /** 从组件（上一个组件） */
    @ApiModelProperty(value = "从组件（上一个组件）")
    @JsonIgnore
    private List<ComponentModel> from;

    /** 组件类型 */
    @ApiModelProperty(value = "组件类型", required = true)
    @JsonIgnore
    private ComponentTypeEnum typeEnum;

    /** 字段映射：key-当前组件字段名称， value-原始表字段名称 */
    @ApiModelProperty(value = "字段映射", required = true)
    private List<String> fields;

    /** 字段映射：left-当前组件字段名称， middle-原始表字段名称， right-原始表名 */
    @ApiModelProperty(value = "字段映射", required = true)
    private List<FieldMappingModel> fieldMappings;

    /** 表名：源组件表名对应真实数据表名，其他组件表名为该组件code */
    @ApiModelProperty(value = "表名", required = true)
    private String tableName;

    /** 表描述：输出组件描述为最终表描述，其他组件描述为组件备注 */
    @ApiModelProperty(value = "表描述", required = true)
    private String tableDesc;

    /** 文件夹id：输出组件文件夹id为数据集的文件夹 */
    @ApiModelProperty(value = "文件夹id")
    private String folderId;

    /** 组件输出查询sql（所有组件） */
    @ApiModelProperty(value = "组件输出查询sql")
    @JsonIgnore
    private String querySql;

    /** 组件预览查询sql（所有组件） */
    @ApiModelProperty(value = "组件预览查询sql")
    @JsonIgnore
    private String previewSql;

    /** 组件输出建表查询sql（输出组件） */
    @ApiModelProperty(value = "组件输出建表查询sql")
    @JsonIgnore
    private String createSql;

    /** 组件输出插入查询sql（输出组件） */
    @ApiModelProperty(value = "组件输出插入查询sql")
    @JsonIgnore
    private String insertSql;

    /** 组件是否处理 */
    @ApiModelProperty(value = "组件是否处理")
    @JsonIgnore
    private boolean handled = false;

    /** 是否为最终结束节点 */
    @ApiModelProperty(value = "是否为最终结束节点")
    @JsonIgnore
    private boolean last = false;

    /** 组件参数 */
    @JsonIgnore
    @ApiModelProperty(value = "组件参数")
    private List<BiComponentParams> params;
}
