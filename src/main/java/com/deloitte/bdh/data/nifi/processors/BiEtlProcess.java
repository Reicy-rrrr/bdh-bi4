package com.deloitte.bdh.data.nifi.processors;

import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.common.util.SpringUtil;
import com.deloitte.bdh.data.enums.ProcessorTypeEnum;
import com.deloitte.bdh.data.model.BiEtlConnection;
import com.deloitte.bdh.data.nifi.dto.ProcessorContext;
import com.deloitte.bdh.data.nifi.connection.Connection;
import com.deloitte.bdh.data.nifi.processor.Processor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;


@Service
public class BiEtlProcess extends AbStractProcessors<ProcessorContext> {

    @Resource(name = "connectionImp")
    private Connection<ProcessorContext> connection;

    @Override
    public ProcessorContext positive(ProcessorContext context) throws Exception {
        logger.info("开始执行创建 BiEtlProcess.positive，参数:{}", JsonUtil.obj2String(context));
        switch (context.getMethod()) {
            case SAVE:
                // 处理processor
                for (int i = 0; i < context.getEnumList().size(); i++) {
                    context.setProcessorSequ(i);
                    SpringUtil.getBean(context.getEnumList().get(i).getType(), Processor.class).pProcess(context);
                }
                context.setProcessorComplete(true);
                // 处理connection
                connection.pConnect(context);
                context.setConnectionComplete(true);
                break;
            case DELETE:
                // 处理connection
                connection.pConnect(context);
                context.setConnectionComplete(true);
                // 处理processor
                for (int i = 0; i < context.getProcessorList().size(); i++) {
                    context.addProcessorTemp(context.getProcessorList().get(i));
                    SpringUtil.getBean(context.getEnumList().get(i).getType(), Processor.class).pProcess(context);
                    context.getHasDelProcessorList().add(context.getTempProcessor());
                    context.removeProcessorTemp();
                }
                context.setProcessorComplete(true);
                break;
            case UPDATE:
                for (ProcessorTypeEnum typeEnum : context.getEnumList()) {
                    SpringUtil.getBean(typeEnum.getType(), Processor.class).pProcess(context);
                }
                context.setProcessorComplete(true);
                break;
            case VALIDATE:
                break;
            default:
                throw new RuntimeException("BiEtlProcess.positive error:不支持的方法");
        }
        return context;
    }

    @Override
    public void reverse(ProcessorContext context) throws Exception {
        logger.info("开始执行 BiEtlProcess.reverse，参数:{}", JsonUtil.obj2String(context));
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
                        //删除临时processor 原因是因为冲正前已有临时 processor
                        context.removeProcessorTemp();
                        context.addProcessorTemp(context.getHasDelProcessorList().get(i));
                        context.setProcessorSequ(i);
                        SpringUtil.getBean(context.getEnumList().get(i).getType(), Processor.class).rProcess(context);
                        context.getNewProcessorList().add(context.getTempProcessor());
                    }
                }
                connection.rConnect(context);
                break;
            case VALIDATE:
            default:
                throw new RuntimeException("BiEtlProcess.reverse error:不支持的方法");
        }
    }

    @Override
    protected void validateContext(ProcessorContext context) throws Exception {
        super.validateContext(context);
        switch (context.getMethod()) {
            case SAVE:
                if (null == context.getBiEtlDatabaseInf() || CollectionUtils.isEmpty(context.getEnumList())
                        || null == context.getProcessors()) {
                    throw new RuntimeException("校验失败:参数不合法");
                }
                break;
            case DELETE:
                if (null == context.getBiEtlDatabaseInf() || CollectionUtils.isEmpty(context.getEnumList())
                        || null == context.getProcessors() || CollectionUtils.isEmpty(context.getProcessorList())) {
                    throw new RuntimeException("校验失败:参数不合法");
                }
                break;
            case UPDATE:
            default:
                throw new RuntimeException("BiEtlProcess.validateContext error:不支持的方法");
        }
    }
}
