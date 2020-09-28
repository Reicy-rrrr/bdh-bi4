package com.deloitte.bdh.data.nifi.processors;

import com.deloitte.bdh.common.util.SpringUtil;
import com.deloitte.bdh.data.enums.ProcessorTypeEnum;
import com.deloitte.bdh.data.nifi.ProcessorContext;
import com.deloitte.bdh.data.nifi.connection.Connection;
import com.deloitte.bdh.data.nifi.processor.Processor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Service
public class BiEtlProcess extends AbStractProcessors {

    @Resource
    private Connection connection;

    @Override
    public ProcessorContext positive(ProcessorContext context) throws Exception {
        // 处理processor
        List<ProcessorTypeEnum> enumList = context.getEnumList();
        for (ProcessorTypeEnum typeEnum : enumList) {
            SpringUtil.getBean(typeEnum.getType(), Processor.class).pProcess(context);
        }
        context.setProcessComplete(true);

        // 处理connection
        for (ProcessorTypeEnum typeEnum : enumList) {
            Map<String, Object> respConnect = connection.pConnect(context);
            context.getSuccessConnectionMap().add(respConnect);
        }

//        // 处理 db
//        db(context);
        return context;
    }

    @Override
    protected void db(ProcessorContext context) throws Exception {

    }

    @Override
    protected void reverse(ProcessorContext context) throws Exception {
        //先处理connection，后处理processor
        List<Map<String, Object>> connectionList = context.getSuccessConnectionMap();
        if (!CollectionUtils.isEmpty(connectionList)) {
            connection.rConnect(context);
        }
        // 处理processor
        List enumList = context.getProcessorList();
        if(CollectionUtils.isEmpty(enumList)){
//            for (ProcessorTypeEnum typeEnum : enumList) {
//                SpringUtil.getBean(typeEnum.getType(), Processor.class).rProcess(context);
//            }
        }

    }

    @Override
    protected void validateContext(ProcessorContext context) throws Exception {
        super.validateContext(context);
        //todo
    }
}
