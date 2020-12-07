package com.deloitte.bdh.data.analyse.model.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Author:LIJUN
 * Date:10/11/2020
 * Description:
 */
@Data
public class AnalysePageDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    private String id;

    /**
     * 报表编码
     */
    @ApiModelProperty("CODE")
    private String code;

    /**
     * 报表名称
     */
    @ApiModelProperty("NAME")
    private String name;

    /**
     *
     */
    @ApiModelProperty("TYPE")
    private String type;

    /**
     * 是否用户自定义类型CUSTOMER,TYPE1,TYPE2...
     */
    @ApiModelProperty("INIT_TYPE")
    private String initType;

    /**
     * 上级id
     */
    @ApiModelProperty("PARENT_ID")
    private String parentId;

    /**
     * 当前编辑的id
     */
    @ApiModelProperty("EDIT_ID")
    private String editId;

    /**
     * 发布后写入的id,重复发布都修改这个值为当前版本
     */
    @ApiModelProperty("PUBLISH_ID")
    private String publishId;

    /**
     * 报表描述
     */
    @ApiModelProperty("DES")
    private String des;

    /**
     * 报表描述
     */
    @ApiModelProperty("ICON")
    private String icon;

    /**
     * 主页
     */
    @ApiModelProperty("HOMEPAGE")
    private String homePage;
}
