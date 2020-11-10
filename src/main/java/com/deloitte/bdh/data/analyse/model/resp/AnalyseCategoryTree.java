package com.deloitte.bdh.data.analyse.model.resp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseCategory;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AnalyseCategoryTree extends BiUiAnalyseCategory {

    @ApiModelProperty(value = "ID")
    private String id;

    /**
     * 文件夹编码
     */
    @ApiModelProperty(value = "CODE")
    private String code;

    /**
     * 报表名称
     */
    @ApiModelProperty(value = "NAME")
    private String name;

    /**
     * predefined,customer 我的分析,预定义报表
     */
    @ApiModelProperty(value = "TYPE")
    private String type;

    /**
     * 系统初始化,自建
     */
    @ApiModelProperty(value = "INIT_TYPE")
    private String initType;

    /**
     * 上级id
     */
    @ApiModelProperty(value = "PARENT_ID")
    private String parentId;

    /**
     * 文件夹
     */
    @ApiModelProperty(value = "DES")
    private String des;

    /**
     * 报表描述
     */
    @ApiModelProperty(value = "ICON")
    private String icon;

    /**
     * 下级数据
     */
    @ApiModelProperty(value = "下级数据")
    List<AnalyseCategoryTree> children = new ArrayList<>();
}
