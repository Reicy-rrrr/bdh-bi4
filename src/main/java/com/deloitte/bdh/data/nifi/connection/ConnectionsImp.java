package com.deloitte.bdh.data.nifi.connection;


import com.deloitte.bdh.data.nifi.ConnectionsContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ConnectionsImp extends AbstractConnection<ConnectionsContext> {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionsImp.class);


    @Override
    public Map<String, Object> save(ConnectionsContext context) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> rSave(ConnectionsContext context) throws Exception {
        return null;
    }


    @Override
    public Map<String, Object> delete(ConnectionsContext context) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> rDelete(ConnectionsContext context) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> update(ConnectionsContext context) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> rUpdate(ConnectionsContext context) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> validate(ConnectionsContext context) throws Exception {
        return null;
    }

}
