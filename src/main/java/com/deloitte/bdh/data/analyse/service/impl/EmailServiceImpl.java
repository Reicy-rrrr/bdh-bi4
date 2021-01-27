package com.deloitte.bdh.data.analyse.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.cron.CronUtil;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.http.HttpClientUtil;
import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.common.util.SnowFlakeUtil;
import com.deloitte.bdh.data.analyse.constants.AnalyseConstants;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiAnalyseSubscribeMapper;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseSubscribe;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseSubscribeLog;
import com.deloitte.bdh.data.analyse.model.request.EmailDto;
import com.deloitte.bdh.data.analyse.model.request.KafkaEmailDto;
import com.deloitte.bdh.data.analyse.service.AnalysePageSubscribeLogService;
import com.deloitte.bdh.data.analyse.service.EmailService;
import com.deloitte.bdh.data.collation.mq.KafkaMessage;

import lombok.extern.slf4j.Slf4j;

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
    
    private static SnowFlakeUtil idWorker = new SnowFlakeUtil(0, 0);

    @Override
    public void sendEmail(EmailDto dto, String type) throws Exception {
        String requestUrl = portalToolsHost + AnalyseConstants.EMAIL_URL;
        Map<String, Object> params = new HashMap<>();
        params.put("requestId", idWorker.nextId());
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
