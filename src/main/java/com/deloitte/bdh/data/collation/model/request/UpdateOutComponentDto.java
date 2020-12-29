package com.deloitte.bdh.data.collation.model.request;


import com.deloitte.bdh.common.util.NifiProcessUtil;
import com.deloitte.bdh.data.analyse.model.request.SaveResourcePermissionDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 修改输出组件请求参数
 *
 * @author chenghzhang
 * @date 2020-12-03
 */
@ApiModel(description = "修改输出组件请求参数")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOutComponentDto {
    @ApiModelProperty(value = "componentCode", example = "0", required = true)
    @NotNull(message = " 组件编码 不能为空")
    private String componentCode;

    @ApiModelProperty(value = "componentName", example = "0", required = true)
    @NotNull(message = " 组件名称 不能为空")
    private String componentName;

    @ApiModelProperty(value = "tableName", example = "0")
    private String tableName;

    @ApiModelProperty(value = "字段列表", example = "0")
    private List<String> fields;

    @ApiModelProperty(value = "坐标", example = "1")
    private String position = NifiProcessUtil.randPosition();

    @ApiModelProperty(value = "comments", example = "0")
    private String comments;

    @ApiModelProperty(value = "数据集文件夹id", example = "12")
    private String folderId;

    @ApiModelProperty(value = "保存资源权限", example = "保存资源权限", required = true)
    private SaveResourcePermissionDto permissionDto;
}
