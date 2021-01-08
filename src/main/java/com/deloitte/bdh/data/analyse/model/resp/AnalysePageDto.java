package com.deloitte.bdh.data.analyse.model.resp;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

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
     * 上级id
     */
    @ApiModelProperty("CATEGORY_ID")
    private String parentId;

    /**
     * 发布来源报表ID
     */
    @TableField("ORIGIN_PAGE_ID")
    private String originPageId;

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

    @ApiModelProperty("DELOITTE_FLAG")
    private String deloitteFlag;

    @ApiModelProperty(value = "permitted Action")
    private String permittedAction;

    @ApiModelProperty(value = "CREATE_DATE")
    private LocalDateTime createDate;

    @ApiModelProperty(value = "CREATE_USER")
    private String createUser;

    @ApiModelProperty(value = "MODIFIED_DATE")
    private LocalDateTime modifiedDate;

    @ApiModelProperty(value = "MODIFIED_USER")
    private String modifiedUser;
}
