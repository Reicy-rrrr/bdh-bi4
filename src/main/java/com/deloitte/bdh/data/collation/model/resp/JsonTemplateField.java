package com.deloitte.bdh.data.collation.model.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * 文件转换成json格式模板字段
 *
 * @author chenghzhang
 * @date 2020/10/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonTemplateField {
    /** 字段名称 */
    private String name;
    /** 字段类型：默认为["null", "string"] */
    private List<String> type = Arrays.asList("null", "string");

    public JsonTemplateField(String name) {
        this.name = name;
    }
}
