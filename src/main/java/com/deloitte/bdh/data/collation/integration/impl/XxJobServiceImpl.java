package com.deloitte.bdh.data.collation.integration.impl;

import com.deloitte.bdh.common.cron.CronUtil;
import com.deloitte.bdh.common.http.HttpClientUtil;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.collation.integration.XxJobService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class XxJobServiceImpl implements XxJobService {
    @Value("${xxjob.ip}")
    private String ip;

    @Override
    public void add(String modelCode, String callBackAddress, String cron, Map<String, String> params) throws Exception {
        log.info("XxJobServiceImpl.add, modelCode:{} ", modelCode);
        Map<String, Object> reqXxJob = assembleParams(modelCode, callBackAddress, cron, params);
        HttpClientUtil.post(ip + ADD_PATH, null, reqXxJob);
    }

    @Override
    public void addOrUpdate(String modelCode, String callBackAddress, String cron, Map<String, String> params) throws Exception {
        log.info("XxJobServiceImpl.update, modelCode:{} ", modelCode);
        Map<String, Object> reqXxJob = assembleParams(modelCode, callBackAddress, cron, params);
        HttpClientUtil.post(ip + UPDATE_PATH, null, reqXxJob);
    }

    @Override
    public void remove(String modelCode) throws Exception {
        log.info("XxJobServiceImpl.remove, modelCode:{} ", modelCode);
        HttpClientUtil.get(ip + REMOVE_PATH + modelCode);
    }

    @Override
    public void start(String modelCode) throws Exception {
        log.info("XxJobServiceImpl.start, modelCode:{} ", modelCode);
        HttpClientUtil.get(ip + START_PATH + modelCode);
    }

    @Override
    public void stop(String modelCode) throws Exception {
        log.info("XxJobServiceImpl.stop, modelCode:{} ", modelCode);
        HttpClientUtil.get(ip + STOP_PATH + modelCode);
    }

    @Override
    public void trigger(String modelCode) throws Exception {
        log.info("XxJobServiceImpl.trigger, modelCode:{} ", modelCode);
        HttpClientUtil.get(ip + TRIGGER_PATH + modelCode);
    }


    private Map<String, Object> assembleParams(String modelCode, String callBackAddress, String cron, Map<String, String> params) {
        CronUtil.validate(cron);
        Map<String, Object> reqXxJob = Maps.newHashMap();
        reqXxJob.put("jobDesc", modelCode);
        reqXxJob.put("callBackAddress", callBackAddress);
        reqXxJob.put("cron", cron);
        reqXxJob.put("params", params);
        reqXxJob.put("tenantCode", ThreadLocalHolder.getTenantCode());
        reqXxJob.put("projectName", "BDH-BI");
        return reqXxJob;
    }
}
