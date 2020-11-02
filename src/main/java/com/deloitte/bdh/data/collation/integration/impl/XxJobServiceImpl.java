package com.deloitte.bdh.data.collation.integration.impl;

import com.deloitte.bdh.common.http.HttpClientUtil;
import com.deloitte.bdh.data.collation.integration.XxJobService;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class XxJobServiceImpl implements XxJobService {
    private static final Logger logger = LoggerFactory.getLogger(XxJobServiceImpl.class);
    private static final String IP = "http://10.81.128.246:9092";

    @Override
    public void add(String modelCode, String callBackAddress, String cron, Map<String, String> params) throws Exception {
//        {
//            "jobDesc":"1223",
//                "callBackAddress":"2132131:1111",
//                "cron":"5 0/3 * * * ?",
//                "params":{
//            "jobDesc":"1223",
//                    "callBackAddress":"2132131:1111",
//                    "cron":"5 0/3 * * * ?"
//        }
//        }
        logger.info("XxJobServiceImpl.add, modelCode:{} ", modelCode);
        Map<String, Object> reqXxJob = Maps.newHashMap();
        reqXxJob.put("jobDesc", modelCode);
        reqXxJob.put("callBackAddress", callBackAddress);
        reqXxJob.put("cron", cron);
        reqXxJob.put("params", params);
        HttpClientUtil.post(IP + ADD_PATH, null, reqXxJob);
    }

    @Override
    public void update(String modelCode, String callBackAddress, String cron, Map<String, String> params) throws Exception {
        logger.info("XxJobServiceImpl.update, modelCode:{} ", modelCode);
        Map<String, Object> reqXxJob = Maps.newHashMap();
        reqXxJob.put("jobDesc", modelCode);
        reqXxJob.put("callBackAddress", callBackAddress);
        reqXxJob.put("cron", cron);
        reqXxJob.put("params", params);
        HttpClientUtil.post(IP + UPDATE_PATH, null, reqXxJob);
    }

    @Override
    public void remove(String modelCode) throws Exception {
        logger.info("XxJobServiceImpl.remove, modelCode:{} ", modelCode);
        String path = getRequestPath(REMOVE_PATH, modelCode);
        HttpClientUtil.get(IP + path, null, null);
    }

    @Override
    public void start(String modelCode) throws Exception {
        logger.info("XxJobServiceImpl.start, modelCode:{} ", modelCode);
        String path = getRequestPath(REMOVE_PATH, modelCode);
        HttpClientUtil.get(IP + path, null, null);
    }

    @Override
    public void stop(String modelCode) throws Exception {
        logger.info("XxJobServiceImpl.stop, modelCode:{} ", modelCode);
        String path = getRequestPath(REMOVE_PATH, modelCode);
        HttpClientUtil.get(IP + path, null, null);
    }

    private String getRequestPath(String url, String... params) {
        for (String param : params) {
            url = url.replaceFirst("#", param);
        }
        return url;
    }
}
