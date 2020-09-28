package com.deloitte.bdh.data.nifi.connection;

import com.deloitte.bdh.data.nifi.ProcessorContext;
import com.deloitte.bdh.data.nifi.processor.AbstractProcessor;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class AbstractConnection implements Connection {
    private static final Logger logger = LoggerFactory.getLogger(AbstractConnection.class);

    @Override
    public final Map<String, Object> pConnect(ProcessorContext context) {
        Map<String, Object> result = Maps.newHashMap();
        switch (context.getMethod()) {
            case SAVE:
                save(context);
                break;
            case DELETE:
                clear(context);
                delete(context);
                break;
            default:
                logger.error("未找到正确的 Connection 处理器");
        }
        return result;
    }

    @Override
    public final Map<String, Object> rConnect(ProcessorContext context) {
        Map<String, Object> result = Maps.newHashMap();
        switch (context.getMethod()) {
            case SAVE:
                clear(context);
                delete(context);
                break;
            case DELETE:
                save(context);
                break;
            default:
                logger.error("未找到正确的 Connection 处理器");
        }
        return result;
    }

    abstract Map<String, Object> save(ProcessorContext context);

    abstract Map<String, Object> clear(ProcessorContext context);

    abstract Map<String, Object> delete(ProcessorContext context);
}
