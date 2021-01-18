package com.deloitte.bdh.data.collation.model.request;


import com.deloitte.bdh.common.util.NifiProcessUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel(description = "输出组件 请求参数")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutComponentDto {

    @ApiModelProperty(value = "modelId", example = "0", required = true)
    @NotNull(message = " 模板id 不能为空")
    private String modelId;

    @ApiModelProperty(value = "tableDesc", example = "0")
    @NotNull(message = " tableDesc 不能为空")
    private String tableName;

    @ApiModelProperty(value = "字段列表", example = "0")
    private List<String> fields;

    @ApiModelProperty(value = "坐标", example = "1")
    private String position = NifiProcessUtil.randPosition();

    @ApiModelProperty(value = "comments", example = "0")
    private String comments;

    @ApiModelProperty(value = "数据集文件夹id", example = "12")
    private String folderId;

}
