package com.deloitte.bdh.data.collation.model.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.Document;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 文件读取结果
 *
 * @author chenghzhang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileReadResult {
    @ApiModelProperty(value = "数据源id", example = "10", required = true)
    private String dbId;

    @ApiModelProperty(value = "文件信息id", example = "25", required = true)
    private String fileId;

    @ApiModelProperty(value = "表头信息", example = "[id,code,value]", required = true)
    private List<String> headers;

    @ApiModelProperty(value = "字段类型", example = "{id:Integer, code:String, value:Float}", required = true)
    private LinkedHashMap<String, String> columns;

    @ApiModelProperty(value = "预览数据", example = "{[{id:1, code:01, value:01},{id:2, code:02, value:02}]}", required = true)
    private List<Document> lines;
}
