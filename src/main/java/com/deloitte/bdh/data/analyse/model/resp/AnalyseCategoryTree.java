package com.deloitte.bdh.data.analyse.model.resp;

import com.deloitte.bdh.data.analyse.model.BiUiAnalyseCategory;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AnalyseCategoryTree extends BiUiAnalyseCategory {
    /**
     * 树状展开需要字段
     */
    @ApiModelProperty(value = "下级数据")
    List<AnalyseCategoryTree> children = new ArrayList<>();
}
