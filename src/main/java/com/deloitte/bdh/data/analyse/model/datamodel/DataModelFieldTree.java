package com.deloitte.bdh.data.analyse.model.datamodel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DataModelFieldTree extends DataModelField {
    /**
     * 树状展开需要字段
     */
    @ApiModelProperty(value = "下级数据")
    List<DataModelFieldTree> children = new ArrayList<>();

    String modelType;

    String name;

    String folderId;

    String isDimention;

    String isMensure;

    String desc;

    public void addChildren(DataModelFieldTree child) {
        children.add(child);
    }
}
