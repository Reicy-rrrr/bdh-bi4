package com.deloitte.bdh.data.nifi;

import com.deloitte.bdh.data.nifi.processors.Processors;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class EtlProcessSelector implements EtlProcess {
    @Resource(name = "biEtlProcess")
    private Processors biEtlProcess;
    @Resource(name = "biProcess")
    private Processors biProcess;


    @Override
    public Object process(Nifi var) throws Exception {
        if (var instanceof ProcessorContext) {
            biEtlProcess.etl((ProcessorContext) var);
        }
        return var;
    }
}
