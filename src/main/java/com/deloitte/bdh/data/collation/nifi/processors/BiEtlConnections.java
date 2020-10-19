package com.deloitte.bdh.data.collation.nifi.processors;


import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.data.collation.nifi.dto.ConnectionsContext;
import com.deloitte.bdh.data.collation.nifi.connection.Connection;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
                break;
            case DELETE:
                // 处理connection
                connection.pConnect(context);
                break;
            default:
                throw new RuntimeException("BiEtlConnections.positive error:不支持的方法");
        }
        return context;
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
                throw new RuntimeException("BiEtlConnections.reverse error:不支持的方法");
        }
    }

    @Override
    protected void validateContext(ConnectionsContext context) throws Exception {
        super.validateContext(context);
        switch (context.getMethod()) {
            case SAVE:
                if (CollectionUtils.isEmpty(context.getFromProcessorsList()) || CollectionUtils.isEmpty(context.getToProcessorsList())) {
                    throw new RuntimeException("校验失败:参数不合法");
                }
                break;
            case DELETE:
                if (CollectionUtils.isEmpty(context.getConnectionsList()) || CollectionUtils.isEmpty(context.getConnectionList())) {
                    throw new RuntimeException("校验失败:参数不合法");
                }
                break;
            case UPDATE:
            default:
                throw new RuntimeException("BiEtlConnections.validateContext error:不支持的方法");
        }
    }
}