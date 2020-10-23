package com.deloitte.bdh.data.collation.database.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * TableData
 *
 * @author chenghzhang
 * @date 2020/10/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableData {
    /** 总记录数 */
    @ApiModelProperty(value = "总记录数", example = "10001", required = true)
    private long total;

    /** 是否还有翻页 */
    @ApiModelProperty(value = "是否还有翻页", example = "true", required = true)
    private boolean more;

    /** 数据列表 */
    @ApiModelProperty(value = "数据行(数组)", example = "[]", required = true)
    private List<LinkedHashMap<String, Object>> rows;
}