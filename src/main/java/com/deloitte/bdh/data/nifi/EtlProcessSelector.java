package com.deloitte.bdh.data.nifi;

import com.deloitte.bdh.data.nifi.processors.Processors;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class EtlProcessSelector implements EtlProcess {
    @Resource(name = "biEtlProcess")
    private Processors<ProcessorContext> biEtlProcess;
    @Resource(name = "biEtlConnections")
    private Processors<ConnectionsContext> biProcess;


    @Override
    public Nifi process(Nifi var) throws Exception {
        if (var instanceof ProcessorContext) {
            biEtlProcess.etl((ProcessorContext) var);
        }
        if (var instanceof ConnectionsContext) {
            biProcess.etl((ConnectionsContext) var);
        }
        return var;
    }
}
