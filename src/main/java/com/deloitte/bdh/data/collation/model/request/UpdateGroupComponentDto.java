package com.deloitte.bdh.data.collation.model.request;


import com.deloitte.bdh.common.util.NifiProcessUtil;
import com.deloitte.bdh.data.collation.component.model.GroupModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 修改聚合组件请求参数
 *
 * @author chenghzhang
 * @date 2020-12-03
 */
@ApiModel(description = "修改聚合组件请求参数")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateGroupComponentDto {
    @ApiModelProperty(value = "componentCode", example = "0", required = true)
    @NotNull(message = " 组件编码 不能为空")
    private String componentCode;

    @ApiModelProperty(value = "componentName", example = "0", required = true)
    @NotNull(message = " 组件名称 不能为空")
    private String componentName ;

    @ApiModelProperty(value = "字段列表", example = "")
    private List<String> fields;

    @ApiModelProperty(value = "聚合字段字段模型", example = "1")
    private GroupModel groups;

    @ApiModelProperty(value = "坐标", example = "1")
    private String position = NifiProcessUtil.randPosition();
}
