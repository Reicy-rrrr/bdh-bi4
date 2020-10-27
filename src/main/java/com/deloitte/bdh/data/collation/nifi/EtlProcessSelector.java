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
        //处理 Processor(创建组件模板，包含数据同步与数据整理2个组件集合)
        if (var instanceof ProcessorContext) {
            biEtlProcess.etl((ProcessorContext) var);
        }

        //处理 Processors（关联组件集合与组件集合，目前版本应该用不到了）
        if (var instanceof ConnectionsContext) {
            biProcess.etl((ConnectionsContext) var);
        }

        //启动、停止、预览（启动与停止组件集合，目前应该时用不到了，直接用模板启动吧。预览用本地预览不走NIFI）
        if (var instanceof RunContext) {
            biEtlRun.etl((RunContext) var);
        }

        return var;
    }
}
