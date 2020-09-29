package com.deloitte.bdh.data.nifi.connection;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.data.model.BiEtlConnection;
import com.deloitte.bdh.data.model.BiEtlParams;
import com.deloitte.bdh.data.model.request.CreateConnectionDto;
import com.deloitte.bdh.data.nifi.Processor;
import com.deloitte.bdh.data.nifi.ProcessorContext;
import com.deloitte.bdh.data.service.BiEtlConnectionService;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class ConnectionImp extends AbstractConnection {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionImp.class);

    @Resource
    private BiEtlConnectionService etlConnectionService;

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

            CreateConnectionDto createConnectionDto = new CreateConnectionDto();
            createConnectionDto.setCreateUser(MapUtils.getString(context.getReq(), "createUser"));
            createConnectionDto.setTenantId(context.getModel().getTenantId());
            createConnectionDto.setFromProcessorCode(pre.getCode());
            createConnectionDto.setToProcessorCode(next.getCode());

            BiEtlConnection connection = etlConnectionService.createConnection(createConnectionDto);
            context.addConnection(connection);
        }
        return null;
    }

    @Override
    public Map<String, Object> rSave(ProcessorContext context) throws Exception {
        List<BiEtlConnection> connectionList = context.getConnectionListList();
        for (BiEtlConnection connection : connectionList) {
            etlConnectionService.dropConnection(connection.getCode());
            etlConnectionService.delConnection(connection.getCode());
            context.addConnection(connection);
        }
        return null;
    }


    @Override
    public Map<String, Object> delete(ProcessorContext context) throws Exception {

        return null;
    }

    @Override
    public Map<String, Object> rDelete(ProcessorContext context) throws Exception {
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
