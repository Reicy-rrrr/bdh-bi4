package com.deloitte.bdh.data.analyse.service;

import com.deloitte.bdh.data.analyse.model.request.EmailDto;

/**
 * Author:LIJUN
 * Date:16/12/2020
 * Description:
 */
public interface EmailService {

    /**
     * 邮件和短信服务
     * @param dto
     * @param type
     */
    void sendEmail(EmailDto dto, String type) throws Exception;

}
