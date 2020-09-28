package com.deloitte.bdh.data.nifi.processor;

import com.deloitte.bdh.data.integration.NifiProcessService;
import com.deloitte.bdh.data.nifi.MethodEnum;
import com.deloitte.bdh.data.nifi.ProcessorContext;
import com.deloitte.bdh.data.service.BiEtlProcessorService;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public abstract class AbstractProcessor implements Processor {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractProcessor.class);

    @Autowired
    protected NifiProcessService nifiProcessService;
    @Autowired
    protected BiEtlProcessorService processorService;

    @Override
    final public Map<String, Object> pProcess(ProcessorContext context) throws Exception {
        Map<String, Object> result = Maps.newHashMap();
        switch (context.getMethod()) {
            case SAVE:
                save(context);
                break;
            case DELETE:
                delete(context);
                break;
            case UPDATE:
                update(context);
                break;
            case VALIDATE:
                validate(context);
                break;
            default:
                logger.error("未找到正确的 Processor 处理器");
        }
        return result;
    }

    @Override
    final public Map<String, Object> rProcess(ProcessorContext context) throws Exception {
        Map<String, Object> result = Maps.newHashMap();
        switch (context.getMethod()) {
            case SAVE:
                delete(context);
                break;
            case DELETE:
                save(context);
                break;
            case UPDATE:
                update(context);
                break;
            case VALIDATE:
                validate(context);
                break;
            default:
                logger.error("未找到正确的 Processor 处理器");
        }
        return result;
    }

    protected abstract Map<String, Object> save(ProcessorContext context) throws Exception;


    protected Map<String, Object> delete(ProcessorContext context) throws Exception {
        Map<String, Object> source = nifiProcessService.delProcessor(MapUtils.getString(context.getReq(), "id"));
        return source;
    }


    protected abstract Map<String, Object> update(ProcessorContext context) throws Exception;


    protected abstract Map<String, Object> validate(ProcessorContext context) throws Exception;

}
