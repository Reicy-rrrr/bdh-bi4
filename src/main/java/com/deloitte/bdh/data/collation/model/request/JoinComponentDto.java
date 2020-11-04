package com.deloitte.bdh.data.collation.model.request;


import com.deloitte.bdh.common.util.NifiProcessUtil;
import com.deloitte.bdh.data.collation.component.model.JoinModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 关联组件请求参数
 *
 * @author chenghzhang
 * @date  2020-11-03
 */
@ApiModel(description = "关联组件请求参数")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JoinComponentDto {
    @ApiModelProperty(value = "modelId", example = "0", required = true)
    @NotNull(message = " 模板id 不能为空")
    private String modelId;

    @ApiModelProperty(value = "字段列表", example = "0")
    @NotNull(message = " 字段列表 不能为空")
    private List<String> fields;

    @ApiModelProperty(value = "坐标", example = "1")
    private String position = NifiProcessUtil.randPosition();

    @ApiModelProperty(value = "tables", example = "")
    @NotNull(message = " tables 不能为空")
    private List<JoinModel> tables;
}
