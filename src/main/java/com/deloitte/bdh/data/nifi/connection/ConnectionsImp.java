package com.deloitte.bdh.data.nifi.connection;

import java.time.LocalDateTime;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.util.GenerateCodeUtil;
import com.deloitte.bdh.data.model.BiConnections;
import com.deloitte.bdh.data.model.BiEtlConnection;
import com.deloitte.bdh.data.model.BiEtlProcessor;
import com.deloitte.bdh.data.model.BiProcessors;
import com.deloitte.bdh.data.nifi.ConnectionsContext;
import com.deloitte.bdh.data.service.BiConnectionsService;
import com.deloitte.bdh.data.service.BiEtlProcessorService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ConnectionsImp extends AbstractConnection<ConnectionsContext> {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionsImp.class);

    @Autowired
    private BiEtlProcessorService processorService;
    @Autowired
    private BiConnectionsService connectionsService;


    @Override
    public Map<String, Object> save(ConnectionsContext context) throws Exception {
        List<BiProcessors> fromProcessorsList = context.getFromProcessorsList();
        List<BiProcessors> toProcessorsList = context.getToProcessorsList();

        List<BiEtlProcessor> processorList = processorService.list(
                new LambdaQueryWrapper<BiEtlProcessor>()
                        .eq(BiEtlProcessor::getProcessGroupId, context.getModel().getProcessGroupId())
        );

        for (BiProcessors from : fromProcessorsList) {
            List<BiEtlProcessor> fromProcessorList = processorList.stream()
                    .filter(s -> s.getRelProcessorsCode().equals(from.getCode()))
                    .sorted(Comparator.comparing(BiEtlProcessor::getSequence).reversed())
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(processorList)) {
                throw new Exception("ConnectionsImp.save error: 未查询到 fromProcessorList 集合");
            }
            BiEtlProcessor fromProcessor = fromProcessorList.get(0);

            for (BiProcessors to : toProcessorsList) {
                List<BiEtlProcessor> toProcessorList = processorList.stream()
                        .filter(s -> s.getRelProcessorsCode().equals(to.getCode()))
                        .sorted(Comparator.comparing(BiEtlProcessor::getSequence))
                        .collect(Collectors.toList());

                if (CollectionUtils.isEmpty(processorList)) {
                    throw new Exception("ConnectionsImp.save error: 未查询到 toProcessorList 集合");
                }

                BiEtlProcessor toProcessor = toProcessorList.get(0);

                BiConnections connections = new BiConnections();
                connections.setCode(GenerateCodeUtil.genConnects());
                connections.setFromProcessorsCode(from.getCode());
                connections.setToProcessorsCode(to.getCode());
                connections.setRelModelCode(context.getModel().getCode());
                connections.setVersion("1");
                connections.setCreateDate(LocalDateTime.now());
                connections.setCreateUser(MapUtils.getString(context.getReq(), "createUser"));
                connections.setTenantId(context.getModel().getTenantId());

                BiEtlConnection biEtlConnection = super.createConnection(context, connections.getRelModelCode(),
                        connections.getCode(), fromProcessor.getCode(), toProcessor.getCode());
                context.getConnectionList().add(biEtlConnection);

                connectionsService.save(connections);
                context.getConnectionsList().add(connections);

            }

        }
        return null;
    }

    @Override
    public Map<String, Object> rSave(ConnectionsContext context) throws Exception {
        List<BiConnections> connectionsList = context.getConnectionsList();
        for (int i = 0; i < connectionsList.size(); i++) {
            connectionsService.removeById(connectionsList.get(i).getId());
            List<BiEtlConnection> connectionList = context.getConnectionList();
            etlConnectionService.dropConnection(connectionList.get(i));
            etlConnectionService.delConnection(connectionList.get(i));
        }
        return null;
    }


    @Override
    public Map<String, Object> delete(ConnectionsContext context) throws Exception {
        List<BiConnections> connectionsList = context.getConnectionsList();
        for (int i = 0; i < connectionsList.size(); i++) {
            List<BiEtlConnection> connectionList = context.getConnectionList();
            etlConnectionService.dropConnection(connectionList.get(i));
            etlConnectionService.delConnection(connectionList.get(i));
            context.getHasConnectionList().add(connectionList.get(i));

            connectionsService.removeById(connectionsList.get(i).getId());
            context.getHasConnectionsList().add(connectionsList.get(i));
        }
        return null;
    }

    @Override
    public Map<String, Object> rDelete(ConnectionsContext context) throws Exception {
        if (CollectionUtils.isNotEmpty(context.getHasConnectionsList())) {
            for (BiConnections connections : context.getHasConnectionsList()) {
                connectionsService.save(connections);
            }
        }
        if (CollectionUtils.isNotEmpty(context.getHasConnectionList())) {
            for (int i = 0; i < context.getHasConnectionList().size(); i++) {
                BiConnections connections = context.getHasConnectionsList().get(i);
                BiEtlConnection connection = context.getHasConnectionList().get(i);

                super.createConnection(context, connections.getRelModelCode(),
                        connections.getCode(), connection.getFromProcessorCode(), connection.getToProcessorCode());
                connectionsService.save(connections);
            }
        }
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
