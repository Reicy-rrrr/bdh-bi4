package com.deloitte.bdh.data.collation.model.resp;

import com.baomidou.mybatisplus.annotation.TableField;
import com.deloitte.bdh.data.collation.component.model.ComponentModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 组件视图
 *
 * @author chenghzhang
 * @date 2020-11-03
 */
@ApiModel(description = "组件视图")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComponentVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "组件id")
    private String id;

    /**
     * 编码
     */
    @TableField("CODE")
    @ApiModelProperty(value = "组件id")
    private String code;

    /**
     * 组件名称
     */
    @ApiModelProperty(value = "组件名称")
    private String name;

    /**
     * 组件类型
     */
    @ApiModelProperty(value = "组件类型")
    private String type;

    /**
     * 是否有效
     */
    @ApiModelProperty(value = "是否有效")
    private String effect;

    /**
     * 所属模板code
     */
    @ApiModelProperty(value = "所属模板code")
    private String refModelCode;

    /**
     * 版本号
     */
    @ApiModelProperty(value = "版本号")
    private String version;

    /**
     * 坐标
     */
    @ApiModelProperty(value = "坐标")
    private String position;

    /**
     * 组件模型
     */
    @ApiModelProperty(value = "组件模型")
    private ComponentModel model;
}
