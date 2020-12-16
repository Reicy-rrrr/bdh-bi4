package com.deloitte.bdh.data.analyse.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.http.HttpClientUtil;
import com.deloitte.bdh.common.util.SnowflakeIdWorker;
import com.deloitte.bdh.data.analyse.constants.AnalyseConstants;
import com.deloitte.bdh.data.analyse.model.request.EmailDto;
import com.deloitte.bdh.data.analyse.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Author:LIJUN
 * Date:16/12/2020
 * Description:
 */
@Slf4j
@Service
@DS(DSConstant.BI_DB)
public class EmailServiceImpl implements EmailService {
    @Value("${portal.tools.url}")
    private String portalToolsHost;

    @Resource
    private SnowflakeIdWorker snowflakeIdWorker;

    @Override
    public void sendEmail(EmailDto dto, String type) throws Exception {
        String requestUrl = portalToolsHost + AnalyseConstants.EMAIL_URL;
        Map<String, Object> params = new HashMap<>();
        params.put("requestId", snowflakeIdWorker.nextId());
        params.put("modules", "EXPENSE");
        params.put("type", type);
        params.put("template", dto.getTemplate());
        params.put("title", dto.getSubject());
        params.put("params", dto.getParamMap());
        params.put("receive", dto.getEmail());
        params.put("ccList", dto.getCcList());
        params.put("message", "");
        params.put("send", "");
        HttpClientUtil.post(requestUrl, new HashMap<String, Object>(), params);
    }
}
