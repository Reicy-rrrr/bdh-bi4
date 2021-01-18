package com.deloitte.bdh.data.analyse.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Author:LIJUN
 * Date:15/12/2020
 * Description:
 */
@Data
@TableName("BI_UI_ANALYSE_SUBSCRIBE")
public class BiUiAnalyseSubscribe implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    /**
     * 报表id
     */
    @TableField("PAGE_ID")
    private String pageId;

    /**
     * 任务id
     */
    @TableField("TASK_ID")
    private String taskId;

    /**
     * 邮件主题
     */
    @TableField("MAIL_SUBJECT")
    private String mailSubject;

    /**
     * cron表达式
     */
    @TableField("CRON_DATA")
    private String cronData;

    /**
     * 收件人
     */
    @TableField("RECEIVER")
    private String receiver;

    /**
     * 图片地址
     */
    @TableField("IMG_URL")
    private String imgUrl;

    /**
     * 数据访问地址
     */
    @TableField("ACCESS_URL")
    private String accessUrl;

    /**
     * 状态：1-启用，0-禁用
     */
    @TableField("STATUS")
    private String status;

    @TableField("TENANT_ID")
    private String tenantId;

    @TableField(value = "CREATE_DATE", fill = FieldFill.INSERT)
    private LocalDateTime createDate;

    @TableField(value = "CREATE_USER", fill = FieldFill.INSERT)
    private String createUser;

    @TableField(value = "MODIFIED_DATE", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime modifiedDate;

    @TableField(value = "MODIFIED_USER", fill = FieldFill.INSERT_UPDATE)
    private String modifiedUser;

}
