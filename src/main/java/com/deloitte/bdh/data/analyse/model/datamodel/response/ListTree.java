package com.deloitte.bdh.data.analyse.model.datamodel.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Author:LIJUN
 * Date:16/11/2020
 * Description:
 */
@Data
public class ListTree {

    @ApiModelProperty(value = "名称")
    private String title;

    @ApiModelProperty(value = "下级数据")
    private List<ListTree> children;
}
