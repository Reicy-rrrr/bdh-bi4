package com.deloitte.bdh.data.collation.nifi;

import com.deloitte.bdh.data.collation.nifi.dto.ConnectionsContext;
import com.deloitte.bdh.data.collation.nifi.dto.Nifi;
import com.deloitte.bdh.data.collation.nifi.dto.ProcessorContext;
import com.deloitte.bdh.data.collation.nifi.dto.RunContext;
import com.deloitte.bdh.data.collation.nifi.processors.Processors;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class EtlProcessSelector<T extends Nifi> implements EtlProcess<T> {
    @Resource(name = "biEtlProcess")
    private Processors<ProcessorContext> biEtlProcess;
    @Resource(name = "biEtlConnections")
    private Processors<ConnectionsContext> biProcess;
    @Resource(name = "biEtlRun")
    private Processors<RunContext> biEtlRun;

    @Override
    public T process(T var) throws Exception {
        //处理 Processor
        if (var instanceof ProcessorContext) {
            biEtlProcess.etl((ProcessorContext) var);
        }

        //处理 Processors
        if (var instanceof ConnectionsContext) {
            biProcess.etl((ConnectionsContext) var);
        }

        //启动、停止、预览
        if (var instanceof RunContext) {
            biEtlRun.etl((RunContext) var);
        }

        return var;
    }
}
