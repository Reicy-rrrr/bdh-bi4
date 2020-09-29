package com.deloitte.bdh.data.nifi.processors;

import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.common.util.SpringUtil;
import com.deloitte.bdh.data.enums.ProcessorTypeEnum;
import com.deloitte.bdh.data.model.BiEtlConnection;
import com.deloitte.bdh.data.nifi.ProcessorContext;
import com.deloitte.bdh.data.nifi.connection.Connection;
import com.deloitte.bdh.data.nifi.processor.Processor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;


@Service
public class BiEtlProcess extends AbStractProcessors {

    @Resource
    private Connection connection;

    @Override
    public ProcessorContext positive(ProcessorContext context) throws Exception {
        logger.info("开始执行创建 nifi processor，参数:{}", JsonUtil.obj2String(context));
        // 处理processor
        List<ProcessorTypeEnum> enumList = context.getEnumList();
        for (ProcessorTypeEnum typeEnum : enumList) {
            SpringUtil.getBean(typeEnum.getType(), Processor.class).pProcess(context);
        }
        context.setProcessComplete(true);

        // 处理connection
        connection.pConnect(context);
        context.setConnectionComplete(true);
        return context;
    }

    @Override
    protected void reverse(ProcessorContext context) throws Exception {
        logger.info("开始执行创建冲正方法，参数:{}", JsonUtil.obj2String(context));
        //先处理connection，后处理processor
        List<BiEtlConnection> connectionList = context.getConnectionListList();
        if (!CollectionUtils.isEmpty(connectionList)) {
            connection.rConnect(context);
        }
        // 处理processor
        if (!CollectionUtils.isEmpty(context.getProcessorList())) {
            for (int i = 0; i < context.getProcessorList().size(); i++) {
                context.addTemp(context.getProcessorList().get(i));
                SpringUtil.getBean(context.getEnumList().get(i).getType(), Processor.class).rProcess(context);
                context.removeTemp();
            }
        }

    }

    @Override
    protected void validateContext(ProcessorContext context) throws Exception {
        super.validateContext(context);
        //todo
    }
}
