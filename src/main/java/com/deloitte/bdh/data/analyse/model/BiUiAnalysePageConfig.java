package com.deloitte.bdh.data.analyse.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import javax.persistence.Transient;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author bo.wang
 * @since 2020-10-19
 */
@Data
@TableName("BI_UI_ANALYSE_PAGE_CONFIG")
public class BiUiAnalysePageConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    /**
     * 报表ID
     */
    @TableField("PAGE_ID")
    private String pageId;

    /**
     * json配置内容
     */
    @TableField("CONTENT")
    private String content;

    @TableField("IP")
    private String ip;

    @TableField("TENANT_ID")
    private String tenantId;

    @TableField("CREATE_DATE")
    private LocalDateTime createDate;

    @TableField("CREATE_USER")
    private String createUser;

    @TableField("MODIFIED_DATE")
    private LocalDateTime modifiedDate;

    @TableField("MODIFIED_USER")
    private String modifiedUser;

}
