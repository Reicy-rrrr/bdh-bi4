package com.deloitte.bdh.data.collation.model.request;


import com.deloitte.bdh.data.collation.component.model.ArrangeSplitModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 整理组件（拆分）请求参数
 *
 * @author chenghzhang
 * @date 2020-11-09
 */
@ApiModel(description = "整理组件（拆分）请求参数")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArrangeSplitDto extends ArrangeComponentDto {
    @ApiModelProperty(value = "拆分字段", example = "")
    @NotNull(message = " 拆分字段 不能为空")
    private List<ArrangeSplitModel> fields;
}
