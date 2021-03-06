package com.deloitte.bdh.data.analyse.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;


@Data
public class pageComponentDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "组件ID")
    private String componentId;

    @ApiModelProperty(value = "组件ID,批量")
    private List<String> componentIds;

    @ApiModelProperty(value = "content")
    private String content;

    @NotNull
    @ApiModelProperty(value = "父级文件夹")
    private String parentId;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "描述")
    private String describe;

}
