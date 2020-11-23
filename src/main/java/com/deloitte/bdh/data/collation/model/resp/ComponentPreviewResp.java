package com.deloitte.bdh.data.collation.model.resp;

import com.deloitte.bdh.data.collation.database.po.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 组件预览对象
 *
 * @author chenghzhang
 * @date  2020-11-03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComponentPreviewResp implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 字段列表 */
    @ApiModelProperty(value = "字段列表", example = "[]", required = true)
    private List<TableField> columns;

    /** 数据列表 */
    @ApiModelProperty(value = "数据行(数组)", example = "[]", required = true)
    private List<Map<String, Object>> rows;
}
