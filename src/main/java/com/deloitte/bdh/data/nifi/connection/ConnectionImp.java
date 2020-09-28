package com.deloitte.bdh.data.nifi.connection;


import com.deloitte.bdh.data.integration.NifiProcessService;
import com.deloitte.bdh.data.model.resp.Processor;
import com.deloitte.bdh.data.nifi.ProcessorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ConnectionImp extends AbstractConnection {
    protected static final Logger logger = LoggerFactory.getLogger(ConnectionImp.class);


    @Override
    public Map<String, Object> save(ProcessorContext context) {
        List<Processor> processorList = context.getProcessorList();
        if (processorList.size() == 1) {
            return null;
        }
        for (int i = 0; i < processorList.size(); i++) {
            if (i == processorList.size() - 1) {
                continue;
            }
            Processor pre = processorList.get(i);
            Processor next = processorList.get(i + 1);

        }
        return null;
    }

    @Override
    public Map<String, Object> clear(ProcessorContext context) {
        return null;
    }

    @Override
    public Map<String, Object> delete(ProcessorContext context) {
        return null;
    }
}
