package com.deloitte.bdh.data.collation.integration.impl;

import com.deloitte.bdh.common.cron.CronUtil;
import com.deloitte.bdh.common.http.HttpClientUtil;
import com.deloitte.bdh.common.json.JsonUtil;
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
    @Value("${xxjob.transfer.url}")
    private String url;

    @Override
    public String getJob(String jobDesc) {
        log.info("XxJobServiceImpl.add, jobDesc:{} ", jobDesc);
        try {
            Map<String, Object> reqXxJob = Maps.newHashMap();
            reqXxJob.put("jobDesc", jobDesc);
            reqXxJob.put("tenantCode", ThreadLocalHolder.getTenantCode());
            return JsonUtil.readObjToJson(returnCheck(HttpClientUtil.post(url + GET_JOB, null, reqXxJob)));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void add(String modelCode, String callBackAddress, String cron, Map<String, String> params) throws Exception {
        log.info("XxJobServiceImpl.add, modelCode:{} ", modelCode);
        Map<String, Object> reqXxJob = assembleParams(modelCode, callBackAddress, cron, params);
        returnCheck(HttpClientUtil.post(url + ADD_PATH, null, reqXxJob));
    }

    @Override
    public void addOrUpdate(String modelCode, String callBackAddress, String cron, Map<String, String> params) throws Exception {
        log.info("XxJobServiceImpl.update, modelCode:{} ", modelCode);
        Map<String, Object> reqXxJob = assembleParams(modelCode, callBackAddress, cron, params);
        returnCheck(HttpClientUtil.post(url + UPDATE_PATH, null, reqXxJob));
    }

    @Override
    public void remove(String modelCode) throws Exception {
        log.info("XxJobServiceImpl.remove, modelCode:{} ", modelCode);
        Map<String, Object> reqXxJob = Maps.newHashMap();
        reqXxJob.put("jobDesc", modelCode);
        reqXxJob.put("tenantCode", ThreadLocalHolder.getTenantCode());
        HttpClientUtil.post(url + REMOVE_PATH, null, reqXxJob);
    }

    @Override
    public void start(String modelCode) throws Exception {
        log.info("XxJobServiceImpl.start, modelCode:{} ", modelCode);
        Map<String, Object> reqXxJob = Maps.newHashMap();
        reqXxJob.put("jobDesc", modelCode);
        reqXxJob.put("tenantCode", ThreadLocalHolder.getTenantCode());
        HttpClientUtil.post(url + START_PATH, null, reqXxJob);
    }

    @Override
    public void stop(String modelCode) throws Exception {
        log.info("XxJobServiceImpl.stop, modelCode:{} ", modelCode);
        Map<String, Object> reqXxJob = Maps.newHashMap();
        reqXxJob.put("jobDesc", modelCode);
        reqXxJob.put("tenantCode", ThreadLocalHolder.getTenantCode());
        HttpClientUtil.post(url + STOP_PATH, null, reqXxJob);
    }

    @Override
    public void trigger(String modelCode) throws Exception {
        log.info("XxJobServiceImpl.trigger, modelCode:{} ", modelCode);
        Map<String, Object> reqXxJob = Maps.newHashMap();
        reqXxJob.put("jobDesc", modelCode);
        reqXxJob.put("tenantCode", ThreadLocalHolder.getTenantCode());
        HttpClientUtil.post(url + TRIGGER_PATH, null, reqXxJob);
    }

    @Override
    public void triggerParams(String modelCode, Map<String, String> params) throws Exception {
        log.info("XxJobServiceImpl.triggerParams, modelCode:{} ", modelCode);
        Map<String, Object> reqXxJob = Maps.newHashMap();
        reqXxJob.put("jobDesc", modelCode);
        reqXxJob.put("params", params);
        //追加类型
        reqXxJob.put("type", "1");
        HttpClientUtil.post(url + TRIGGER_PARAMS_PATH, null, reqXxJob);
    }

    @Override
    public String getGroupByTenant() {
        log.info("XxJobServiceImpl.getGroupByTenant");
        try {
            Map<String, Object> reqXxJob = Maps.newHashMap();
            reqXxJob.put("tenantCode", ThreadLocalHolder.getTenantCode());
            Map map = returnCheck(HttpClientUtil.post(url + LOAD_BY_TENANT, null, reqXxJob));
            return JsonUtil.readObjToJson(map);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean saveGroup() {
        log.info("XxJobServiceImpl.saveGroup");
        try {
            Map<String, Object> reqXxJob = Maps.newHashMap();
            reqXxJob.put("tenantCode", ThreadLocalHolder.getTenantCode());
            returnCheck(HttpClientUtil.post(url + saveGroup, null, reqXxJob));
            return true;
        } catch (Exception e) {
            return false;
        }
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

    private Map returnCheck(String str) {
        Map map = JsonUtil.JsonStrToMap(str);
        String code = (String) map.get("code");
        if (!"200".equals(code)) {
            throw new RuntimeException((String) map.get("msg"));
        }
        return map;
    }
}
