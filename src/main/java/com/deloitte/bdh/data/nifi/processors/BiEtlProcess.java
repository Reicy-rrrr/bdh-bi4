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
        switch (context.getMethod()) {
            case SAVE:
                // 处理processor
                for (ProcessorTypeEnum typeEnum : context.getEnumList()) {
                    SpringUtil.getBean(typeEnum.getType(), Processor.class).pProcess(context);
                }
                context.setProcessorComplete(true);
                // 处理connection
                connection.pConnect(context);
                context.setConnectionComplete(true);
                return context;

            case DELETE:
                // 处理connection
                connection.pConnect(context);
                context.setConnectionComplete(true);
                // 处理processor
                for (int i = 0; i < context.getProcessorList().size(); i++) {
                    context.addProcessorTemp(context.getProcessorList().get(i));
                    //模拟报错
//                    if (i == 1) {
//                        int ti = 1 / 0;
//                    }
                    SpringUtil.getBean(context.getEnumList().get(i).getType(), Processor.class).pProcess(context);
                    context.getHasDelProcessorList().add(context.getTempProcessor());
                    context.removeProcessorTemp();
                }
                context.setProcessorComplete(true);
                return context;
            case UPDATE:
                // 处理processor
                for (ProcessorTypeEnum typeEnum : context.getEnumList()) {
                    SpringUtil.getBean(typeEnum.getType(), Processor.class).pProcess(context);
                }
                context.setProcessorComplete(true);
            case VALIDATE:
                break;
            default:

        }

        return null;
    }

    @Override
    protected void reverse(ProcessorContext context) throws Exception {
        logger.info("开始执行创建冲正方法，参数:{}", JsonUtil.obj2String(context));
        switch (context.getMethod()) {
            case SAVE:
                //先处理connection，后处理processor
                List<BiEtlConnection> connectionList = context.getConnectionList();
                if (!CollectionUtils.isEmpty(connectionList)) {
                    connection.rConnect(context);
                }
                // 处理processor
                if (!CollectionUtils.isEmpty(context.getProcessorList())) {
                    for (int i = 0; i < context.getProcessorList().size(); i++) {
                        context.addProcessorTemp(context.getProcessorList().get(i));
                        SpringUtil.getBean(context.getEnumList().get(i).getType(), Processor.class).rProcess(context);
                        context.removeProcessorTemp();
                    }
                }
                break;

            case UPDATE:
                break;

            case DELETE:
                if (!CollectionUtils.isEmpty(context.getHasDelProcessorList())) {
                    // 说明删除connection成功，先处理processor
                    for (int i = 0; i < context.getHasDelProcessorList().size(); i++) {
                        context.addProcessorTemp(context.getHasDelProcessorList().get(i));
                        SpringUtil.getBean(context.getEnumList().get(i).getType(), Processor.class).rProcess(context);
                        context.getNewProcessorList().add(context.getTempProcessor());
                        context.removeProcessorTemp();
                    }
                }
                connection.rConnect(context);
                break;
            case VALIDATE:
            default:
                break;

        }
    }

    @Override
    protected void validateContext(ProcessorContext context) throws Exception {
        super.validateContext(context);
        //todo
    }
}
