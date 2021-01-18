package com.deloitte.bdh.data.analyse.model.resp;

import com.baomidou.mybatisplus.annotation.*;
import com.deloitte.bdh.data.analyse.model.request.UserIdMailDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Author:LIJUN
 * Date:15/12/2020
 * Description:
 */
@Data
public class AnalyseSubscribeLogDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    private String id;

    /**
     * 报表id
     */
    @ApiModelProperty(value = "PAGE_ID")
    private String pageId;

    /**
     * 邮件主题
     */
    @ApiModelProperty(value = "MAIL_SUBJECT")
    private String mailSubject;

    /**
     * cron表达式
     */
    @ApiModelProperty(value = "CRON")
    private String cron;

    /**
     * cron表达式描述
     */
    @ApiModelProperty(value = "CRON_DESC")
    private String cronDesc;

    /**
     * 收件人
     */
    @ApiModelProperty(value = "RECEIVER")
    private UserIdMailDto receiver;

    /**
     * 失败描述信息
     */
    @ApiModelProperty(value = "FAIL_MESSAGE")
    private String failMessage;

    /**
     * 状态：1-成功，0-失败
     */
    @ApiModelProperty(value = "EXECUTE_STATUS")
    private String executeStatus;

}
