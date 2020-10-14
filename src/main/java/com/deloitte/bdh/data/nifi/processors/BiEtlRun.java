package com.deloitte.bdh.data.nifi.processors;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.data.enums.RunStatusEnum;
import com.deloitte.bdh.data.integration.NifiProcessService;
import com.deloitte.bdh.data.model.BiEtlConnection;
import com.deloitte.bdh.data.model.BiEtlProcessor;
import com.deloitte.bdh.data.model.BiProcessors;
import com.deloitte.bdh.data.nifi.dto.RunContext;
import com.deloitte.bdh.data.service.*;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BiEtlRun extends AbStractProcessors<RunContext> {
    @Autowired
    private BiEtlProcessorService processorService;
    @Autowired
    private BiProcessorsService processorsService;
    @Autowired
    private BiEtlConnectionService biEtlConnectionService;
    @Autowired
    private NifiProcessService nifiProcessService;

    @Override
    public RunContext positive(RunContext context) throws Exception {
        logger.info("开始执行创建 BiEtlRun.positive，参数:{}", JsonUtil.obj2String(context));
        switch (context.getMethod()) {
            case VIEW:
                preview(context);
                stopAndClear(context.getModel().getProcessGroupId(), context.getModel().getCode());
                break;
            case RUN:
                nifiProcessService.runState(context.getModel().getProcessGroupId(), RunStatusEnum.RUNNING.getKey(), true);
                break;
            case STOP:
                stopAndClear(context.getModel().getProcessGroupId(), context.getModel().getCode());
                break;
            default:
        }
        return null;
    }

    @Override
    public void reverse(RunContext context) throws Exception {
        logger.info("开始执行创建 BiEtlRun.reverse，参数:{}", JsonUtil.obj2String(context));
        switch (context.getMethod()) {
            case VIEW:
                stopAndClear(context.getModel().getProcessGroupId(), context.getModel().getCode());
                break;
            case RUN:
                logger.info("BiEtlRun.reverse.RUN : nothing");
                break;
            case STOP:
                logger.info("BiEtlRun.reverse.STOP : nothing");
                break;
            default:

        }
    }

    @Override
    protected void validateContext(RunContext context) throws Exception {
    }

    private void preview(RunContext context) throws Exception {
        String result;
        //获取所有的processors 集合
        List<BiProcessors> processorsList = processorsService.getPreChain(context.getPreviewCode());

        //获取processors 下面所有processor 以及需要查询的 connection
        List<BiEtlProcessor> processorList = Lists.newLinkedList();
        List<BiEtlConnection> connectionList = Lists.newLinkedList();
        processorsList.forEach(s -> {
            List<BiEtlProcessor> var = processorService.list(
                    new LambdaQueryWrapper<BiEtlProcessor>().eq(BiEtlProcessor::getRelProcessorsCode, s.getCode())
                            .orderByAsc(BiEtlProcessor::getSequence)
            );

            if (s.getCode().equals(context.getPreviewCode())) {
                //移除最后一个 processor
                BiEtlProcessor lastProcessor = var.get(var.size() - 1);
                var.remove(var.size() - 1);
                //获取最后一个 processor 上的 connection
                BiEtlConnection etlConnection = biEtlConnectionService.getOne(
                        new LambdaQueryWrapper<BiEtlConnection>()
                                .eq(BiEtlConnection::getToProcessorCode, lastProcessor.getCode())
                                .ne(BiEtlConnection::getFromProcessorCode, lastProcessor.getCode())
                );
                connectionList.add(etlConnection);
            }
            processorList.addAll(var);
        });

        //启动
        for (BiEtlProcessor var : processorList) {
            nifiProcessService.runState(var.getProcessId(), RunStatusEnum.RUNNING.getKey(), false);
        }

        //让数据生成目前设置3秒
        Thread.sleep(3000);
        result = nifiProcessService.preview(connectionList.get(0).getConnectionId());
        context.setResult(result);
    }


    @Async
    public void stopAndClear(String processGroupId, String modelCode) throws Exception {
        //停止
        nifiProcessService.runState(processGroupId, RunStatusEnum.STOP.getKey(), true);

        //清空所有
        List<BiEtlConnection> connectionList = biEtlConnectionService.list(
                new LambdaQueryWrapper<BiEtlConnection>().eq(BiEtlConnection::getRelModelCode, modelCode)
        );
        for (BiEtlConnection var : connectionList) {
            nifiProcessService.dropConnections(var.getConnectionId());
        }
    }

}
