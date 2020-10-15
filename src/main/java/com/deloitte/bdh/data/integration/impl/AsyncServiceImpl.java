package com.deloitte.bdh.data.integration.impl;

import com.deloitte.bdh.data.enums.RunStatusEnum;
import com.deloitte.bdh.data.integration.AsyncService;
import com.deloitte.bdh.data.integration.NifiProcessService;
import com.deloitte.bdh.data.model.BiEtlConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AsyncServiceImpl implements AsyncService {
    private static final Logger logger = LoggerFactory.getLogger(AsyncServiceImpl.class);

    @Autowired
    private NifiProcessService nifiProcessService;

    @Override
    @Async("taskExecutor")
    public void stopAndClearAsync(String processGroupId, String modelCode, List<BiEtlConnection> connectionList) throws Exception {
        logger.info("开始异步停止与清空组件");
        this.stopAndClearSync(processGroupId, modelCode, connectionList);
    }

    @Override
    public void stopAndClearSync(String processGroupId, String modelCode, List<BiEtlConnection> connectionList) throws Exception {
        //停止
        nifiProcessService.runState(processGroupId, RunStatusEnum.STOP.getKey(), true);
        //清空所有
        for (BiEtlConnection var : connectionList) {
            nifiProcessService.dropConnections(var.getConnectionId());
        }
    }
}
