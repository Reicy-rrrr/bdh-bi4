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
@TableName("BI_UI_ANALYSE_SUBSCRIBE_LOG")
public class BiUiAnalyseSubscribeLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    /**
     * 报表id
     */
    @TableField("PAGE_ID")
    private String pageId;

    /**
     * cron表达式
     */
    @TableField("CRON")
    private String cron;

    /**
     * cron表达式描述
     */
    @TableField("CRON_DESC")
    private String cronDesc;

    /**
     * 收件人
     */
    @TableField("RECEIVER")
    private String receiver;

    /**
     * 失败描述信息
     */
    @TableField("FAIL_MESSAGE")
    private String failMessage;

    /**
     * 状态：1-成功，0-失败
     */
    @TableField("EXECUTE_STATUS")
    private String executeStatus;

    @TableField(value = "CREATE_DATE", fill = FieldFill.INSERT)
    private LocalDateTime createDate;

    @TableField(value = "CREATE_USER", fill = FieldFill.INSERT)
    private String createUser;

    @TableField(value = "MODIFIED_DATE", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime modifiedDate;

    @TableField(value = "MODIFIED_USER", fill = FieldFill.INSERT_UPDATE)
    private String modifiedUser;

}
