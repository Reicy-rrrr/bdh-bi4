package com.deloitte.bdh.data.collation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 文件转换成json格式的模板
 *
 * @author chenghzhang
 * @date 2020/10/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonTemplate {
    /**
     * json类型：固定为record，不可修改
     */
    private String type = "record";
    /**
     * json名称：默认Record
     */
    private String name = "Record";
    /**
     * 文件中字段：与数据表中一致
     */
    private List<JsonTemplateField> fields;
}
