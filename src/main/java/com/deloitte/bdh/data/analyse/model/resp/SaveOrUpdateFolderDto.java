package com.deloitte.bdh.data.analyse.model.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;
import java.util.List;


@Data
public class SaveOrUpdateFolderDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID", example = "")
    private String id;

    /**
     * 数据模型id
     */
    @NotBlank
    @ApiModelProperty(value = "数据模型id", example = "")
    private String modelId;

    /**
     * 上级id
     */
    @NotBlank
    @ApiModelProperty(value = "上级id", example = "")
    private String parentId;

    /**
     * 名称
     */
    @NotBlank
    @ApiModelProperty(value = "名称", example = "")
    private String name;

    /**
     * 类型
     */
    @NotBlank
    @ApiModelProperty(value = "类型", example = "")
    private String type;

}
