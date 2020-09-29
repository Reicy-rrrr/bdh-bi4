package com.deloitte.bdh.data.nifi;

import com.deloitte.bdh.data.enums.ProcessorTypeEnum;
import com.deloitte.bdh.data.model.BiEtlConnection;
import com.deloitte.bdh.data.model.BiEtlDatabaseInf;
import com.deloitte.bdh.data.model.BiEtlModel;
import com.deloitte.bdh.data.model.BiProcessors;
import com.deloitte.bdh.data.nifi.enums.MethodEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.*;

import java.util.List;
import java.util.Map;


@Data
public class ProcessorContext {
    private List<ProcessorTypeEnum> enumList;
    private Map<String, Object> req = Maps.newHashMap();
    private MethodEnum method;

    private BiEtlModel model = new BiEtlModel();
    private BiEtlDatabaseInf biEtlDatabaseInf = new BiEtlDatabaseInf();
    private BiProcessors processors = new BiProcessors();

    private List<Processor> processorList = null;
    private Processor tempProcessor = null;

    private List<BiEtlConnection> connectionListList = null;
    private Processor tempConnection = null;


    private List<Map<String, Object>> successProcessMap = Lists.newArrayList();
    private Boolean processComplete = false;

    private List<Map<String, Object>> successConnectionMap;
    private Boolean connectionComplete = false;


    public ProcessorContext addTemp(Processor processor) {
        if (null != this.tempProcessor) {
            throw new RuntimeException("未移除上个处理的临时temp");
        }
        this.tempProcessor = processor;
        return this;
    }

    public ProcessorContext removeTemp() {
        this.tempProcessor = null;
        this.tempConnection = null;
        return this;
    }

    public ProcessorContext addProcessor(Processor processor) {
        if (null == this.processorList) {
            this.processorList = Lists.newLinkedList();
        }
        this.processorList.add(processor);
        return this;
    }

    public ProcessorContext addConnection(BiEtlConnection connection) {
        if (null == this.connectionListList) {
            this.connectionListList = Lists.newLinkedList();
        }
        this.connectionListList.add(connection);
        return this;
    }
}
