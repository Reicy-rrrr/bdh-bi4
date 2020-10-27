package com.deloitte.bdh.data.collation.nifi.connection;


import com.deloitte.bdh.data.collation.enums.ProcessorTypeEnum;
import com.deloitte.bdh.data.collation.model.BiEtlConnection;
import com.deloitte.bdh.data.collation.nifi.dto.Processor;
import com.deloitte.bdh.data.collation.nifi.dto.ProcessorContext;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ConnectionImp extends AbstractConnection<ProcessorContext> {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionImp.class);


    @Override
    public Map<String, Object> save(ProcessorContext context) throws Exception {
        //能进来这里肯定是全部processor 创建成功
        List<Processor> processorList = context.getProcessorList();
        for (int i = 0; i < processorList.size(); i++) {
            String fromCode, toCode;
            if (i == processorList.size() - 1) {
                if (toConnection(processorList.get(i))) {
                    continue;
                }
                fromCode = processorList.get(i).getCode();
                toCode = fromCode;
            } else {
                fromCode = processorList.get(i).getCode();
                toCode = processorList.get(i + 1).getCode();
            }
            BiEtlConnection connection = super.createConnection(context, context.getProcessors().getRelModelCode(),
                    context.getProcessors(), fromCode, toCode);
            context.addConnectionList(connection);
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
            for (int i = 0; i < newProcessorList.size(); i++) {
                String fromCode, toCode;
                if (i == newProcessorList.size() - 1) {
                    if (toConnection(newProcessorList.get(i))) {
                        continue;
                    }
                    fromCode = newProcessorList.get(i).getCode();
                    toCode = fromCode;
                } else {
                    fromCode = newProcessorList.get(i).getCode();
                    toCode = newProcessorList.get(i + 1).getCode();
                }
                BiEtlConnection connection = super.createConnection(context, context.getProcessors().getRelModelCode(),
                        context.getProcessors(), fromCode, toCode);
                context.addConnectionList(connection);
            }
        } else {
            //说明删除connection 就发生部分失败,找到已删除的connecion
            List<BiEtlConnection> connectionList = context.getHasDelConnectionList();
            for (BiEtlConnection connection : connectionList) {
                super.createConnection(context, context.getProcessors().getRelModelCode(),
                        context.getProcessors(), connection.getFromProcessorCode(), connection.getToProcessorCode());
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

    private boolean toConnection(Processor processor) {
        //todo 增加 需要自连接的 processor，目前只有 putsql ?
        if (ProcessorTypeEnum.PutSQL.getType().equals(processor.getType())) {
            return false;
        }
        return true;
    }
}
