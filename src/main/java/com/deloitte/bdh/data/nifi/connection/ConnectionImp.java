package com.deloitte.bdh.data.nifi.connection;


import com.deloitte.bdh.data.nifi.ProcessorContext;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ConnectionImp extends AbstractConnection {
    @Override
    public Map<String, Object> save(ProcessorContext context) {
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
