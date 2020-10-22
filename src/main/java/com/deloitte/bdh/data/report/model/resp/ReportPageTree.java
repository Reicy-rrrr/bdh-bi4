package com.deloitte.bdh.data.report.model.resp;

import com.deloitte.bdh.data.report.model.BiUiReportPage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ReportPageTree extends BiUiReportPage {
    /**
     * 树状展开需要字段
     */
    @ApiModelProperty(value = "下级数据")
    List<ReportPageTree> children = new ArrayList<>();
}
