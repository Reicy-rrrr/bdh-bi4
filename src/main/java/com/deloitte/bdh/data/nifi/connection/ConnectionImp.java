package com.deloitte.bdh.data.nifi.connection;


import com.deloitte.bdh.data.model.BiEtlConnection;
import com.deloitte.bdh.data.nifi.Processor;
import com.deloitte.bdh.data.nifi.ProcessorContext;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ConnectionImp extends AbstractConnection {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionImp.class);


    @Override
    public Map<String, Object> save(ProcessorContext context) throws Exception {
        //能进来这里肯定是全部processor 创建成功
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
            BiEtlConnection connection = super.createConnection(context, pre.getCode(), next.getCode());
            context.addConnection(connection);
        }
        return null;
    }

    @Override
    public Map<String, Object> rSave(ProcessorContext context) throws Exception {
        List<BiEtlConnection> connectionList = context.getConnectionList();
        for (BiEtlConnection connection : connectionList) {
            etlConnectionService.dropConnection(connection);
            etlConnectionService.delConnection(connection);
        }
        return null;
    }


    @Override
    public Map<String, Object> delete(ProcessorContext context) throws Exception {
        List<BiEtlConnection> connectionList = context.getConnectionList();
        if (CollectionUtils.isNotEmpty(connectionList)) {
            for (BiEtlConnection connection : connectionList) {
                etlConnectionService.dropConnection(connection);
                etlConnectionService.delConnection(connection);
                context.getHasDelConnectionList().add(connection);
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> rDelete(ProcessorContext context) throws Exception {
        if (!CollectionUtils.isEmpty(context.getNewProcessorList())) {
            List<Processor> newProcessorList = assemblyNewProcessorList(context);
            // 说明删除connection成功，先处理processor
            if (newProcessorList.size() == 1) {
                return null;
            }
            for (int i = 0; i < newProcessorList.size(); i++) {
                if (i == newProcessorList.size() - 1) {
                    continue;
                }
                Processor pre = newProcessorList.get(i);
                Processor next = newProcessorList.get(i + 1);
                super.createConnection(context, pre.getCode(), next.getCode());
            }
        } else {
            //说明删除connection 就发生部分失败,找到已删除的connecion
            List<BiEtlConnection> connectionList = context.getHasDelConnectionList();
            for (BiEtlConnection connection : connectionList) {
                super.createConnection(context, connection.getFromProcessorCode(), connection.getToProcessorCode());
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> update(ProcessorContext context) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> rUpdate(ProcessorContext context) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> validate(ProcessorContext context) throws Exception {
        return null;
    }
}
