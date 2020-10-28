package com.deloitte.bdh.data.collation.nifi;

import com.deloitte.bdh.data.collation.nifi.dto.ConnectionsContext;
import com.deloitte.bdh.data.collation.nifi.dto.ProcessorContext;
import com.deloitte.bdh.data.collation.nifi.processors.Processors;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class EtlProcessSelector implements EtlProcess {
    @Resource(name = "biEtlProcess")
    private Processors<ProcessorContext> biEtlProcess;
    @Resource(name = "biEtlConnections")
    private Processors<ConnectionsContext> biProcess;

    @Override
    public ProcessorContext operateProcessorGroup(ProcessorContext var) throws Exception {
        //处理 Processor(创建组件模板，包含数据同步与数据整理2个组件集合)
        return biEtlProcess.etl(var);
    }

    @Override
    public ConnectionsContext operateProcessorGroupConnections(ConnectionsContext var) throws Exception {
        //处理 Processors（关联组件集合与组件集合，目前版本应该用不到了）
        return biProcess.etl(var);
    }
}
