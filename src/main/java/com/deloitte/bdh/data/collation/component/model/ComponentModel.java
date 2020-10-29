package com.deloitte.bdh.data.collation.component.model;

import com.deloitte.bdh.data.collation.enums.ComponentTypeEnum;
import com.deloitte.bdh.data.collation.model.BiComponentParams;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

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
    private String id;

    /** 组件code */
    @ApiModelProperty(value = "组件code", required = true)
    private String code;

    /** 组件类型 */
    @ApiModelProperty(value = "组件类型", example = "10")
    private String type;

    /** 所属模板code */
    @ApiModelProperty(value = "所属模板code", example = "10")
    private String refModelCode;

    /** 从组件（上一个组件） */
    @ApiModelProperty(value = "从组件（上一个组件）")
    private List<ComponentModel> from;

    /** 组件类型 */
    @ApiModelProperty(value = "组件类型", required = true)
    private ComponentTypeEnum typeEnum;

    /** 字段映射：key-当前组件字段名称， value-原始表字段名称 */
    @ApiModelProperty(value = "字段映射", required = true)
    private List<String> fields;

    /** 字段映射：key-当前组件字段名称， value-原始表字段名称 */
    @ApiModelProperty(value = "字段映射", required = true)
    private List<Triple> fieldMappings;

    /** 表名：源组件表名对应真实数据表名，其他组件表名为该组件code */
    @ApiModelProperty(value = "表名", required = true)
    private String tableName;

    /** 组件输出sql */
    @ApiModelProperty(value = "组件输出sql")
    private String sql;

    /** 组件是否处理 */
    @ApiModelProperty(value = "组件是否处理")
    private boolean handled = false;

    /** 是否为最终结束节点 */
    @ApiModelProperty(value = "是否为最终结束节点")
    private boolean last = false;

    /** 组件参数 */
    @ApiModelProperty(value = "组件参数")
    private List<BiComponentParams> params;
}
