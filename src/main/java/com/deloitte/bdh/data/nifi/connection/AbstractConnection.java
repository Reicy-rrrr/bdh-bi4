package com.deloitte.bdh.data.nifi.connection;

import com.deloitte.bdh.data.model.BiEtlConnection;
import com.deloitte.bdh.data.nifi.Nifi;
import com.deloitte.bdh.data.nifi.dto.CreateConnectionDto;
import com.deloitte.bdh.data.nifi.Processor;
import com.deloitte.bdh.data.nifi.ProcessorContext;
import com.deloitte.bdh.data.nifi.processor.AbstractCurdProcessor;
import com.deloitte.bdh.data.service.BiEtlConnectionService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

public abstract class AbstractConnection<T extends Nifi> extends AbstractCurdProcessor<T> implements Connection<T> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractConnection.class);
    @Resource
    protected BiEtlConnectionService etlConnectionService;

    @Override
    public final Map<String, Object> pConnect(T context) throws Exception {
        Map<String, Object> result = Maps.newHashMap();
        switch (context.getMethod()) {
            case SAVE:
                save(context);
                break;
            case DELETE:
                delete(context);
                break;
            default:
                logger.error("未找到正确的 Connection 处理器");
        }
        return result;
    }

    @Override
    public final Map<String, Object> rConnect(T context) throws Exception {
        Map<String, Object> result = Maps.newHashMap();
        switch (context.getMethod()) {
            case SAVE:
                rSave(context);
                break;
            case DELETE:
                rDelete(context);
                break;
            default:
                logger.error("未找到正确的 Connection 处理器");
        }
        return result;
    }


    final protected BiEtlConnection createConnection(ProcessorContext context, String fromCode, String toCode) throws Exception {
        CreateConnectionDto createConnectionDto = new CreateConnectionDto();
        createConnectionDto.setCreateUser(MapUtils.getString(context.getReq(), "createUser"));
        createConnectionDto.setTenantId(context.getModel().getTenantId());
        createConnectionDto.setFromProcessorCode(fromCode);
        createConnectionDto.setToProcessorCode(toCode);
        createConnectionDto.setProcessors(context.getProcessors());
        return etlConnectionService.createConnection(createConnectionDto);
    }

    final protected List<Processor> assemblyNewProcessorList(ProcessorContext context) {
        List<Processor> result = Lists.newLinkedList();
        List<Processor> newProcessorList = context.getNewProcessorList();
        List<Processor> processorList = context.getProcessorList();
        for (int i = 0; i < processorList.size(); i++) {
            if (i < newProcessorList.size()) {
                result.add(newProcessorList.get(i));
            } else {
                result.add(processorList.get(i));
            }
        }
        return result;
    }

}
