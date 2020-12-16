package com.deloitte.bdh.data.analyse.model.resp;

import com.baomidou.mybatisplus.annotation.*;
import com.deloitte.bdh.data.analyse.model.request.UserIdMailDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Author:LIJUN
 * Date:15/12/2020
 * Description:
 */
@Data
public class AnalyseSubscribeDto implements Serializable {

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
     * 收件人
     */
    @ApiModelProperty(value = "RECEIVER")
    private List<UserIdMailDto> receiver;

    /**
     * 数据访问地址
     */
    @ApiModelProperty(value = "ACCESS_URL")
    private String accessUrl;

    /**
     * 状态：1-启用，0-禁用
     */
    @ApiModelProperty(value = "STATUS")
    private String status;

}
