package com.deloitte.bdh.data.collation.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetDataSetByCodeDto {
    /**
     * 数据表名称
     */
    @ApiModelProperty(value = "code list", required = true)
    @NotEmpty(message = "code数组不能为空")
    private List<String> codeList;

}
