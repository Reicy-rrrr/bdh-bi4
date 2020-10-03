package com.deloitte.bdh.data.nifi.processors;


import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.data.nifi.ConnectionsContext;
import com.deloitte.bdh.data.nifi.connection.Connection;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class BiEtlConnections extends AbStractProcessors<ConnectionsContext> {


    @Resource(name = "connectionsImp")
    private Connection<ConnectionsContext> connection;

    @Override
    public ConnectionsContext positive(ConnectionsContext context) throws Exception {
        logger.info("开始执行创建 BiConnections.positive，参数:{}", JsonUtil.obj2String(context));
        switch (context.getMethod()) {
            case SAVE:
                // 处理connection
                connection.pConnect(context);
                return context;

            case DELETE:
                // 处理connection
                connection.pConnect(context);
                return context;
            default:

        }
        return null;
    }

    @Override
    public void reverse(ConnectionsContext context) throws Exception {
        logger.info("开始执行创建 BiConnections.reverse，参数:{}", JsonUtil.obj2String(context));
        switch (context.getMethod()) {
            case SAVE:
                // 处理connection
                connection.rConnect(context);
                break;
            case DELETE:
                // 处理connection
                connection.rConnect(context);
                break;
            default:

        }
    }

    @Override
    protected void validateContext(ConnectionsContext context) throws Exception {
        super.validateContext(context);
        //todo
    }
}
