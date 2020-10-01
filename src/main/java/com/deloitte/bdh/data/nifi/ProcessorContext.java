package com.deloitte.bdh.data.nifi;

import com.deloitte.bdh.data.enums.ProcessorTypeEnum;
import com.deloitte.bdh.data.model.*;
import com.deloitte.bdh.data.nifi.enums.MethodEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.*;

import java.util.List;
import java.util.Map;


@Data
public class ProcessorContext {
    private List<ProcessorTypeEnum> enumList;
    private MethodEnum method;
    private Map<String, Object> req = Maps.newHashMap();

    private BiEtlModel model = new BiEtlModel();
    private BiEtlDatabaseInf biEtlDatabaseInf = new BiEtlDatabaseInf();
    private BiProcessors processors = new BiProcessors();

    private List<Processor> processorList = null;
    private Processor tempProcessor = null;

    private List<BiEtlConnection> connectionList = null;
    private BiEtlConnection tempConnection = null;


    private Boolean processorComplete = false;
    private Boolean connectionComplete = false;

    //删除才有
    private List<Processor> hasDelProcessorList = Lists.newLinkedList();
    private List<BiEtlConnection> hasDelConnectionList = Lists.newLinkedList();


    public ProcessorContext addTemp(Processor processor) {
        if (null != this.tempProcessor) {
            throw new RuntimeException("未移除上个处理的临时temp");
        }
        this.tempProcessor = processor;
        return this;
    }

    public ProcessorContext removeProcessorTemp() {
        this.tempProcessor = null;
        return this;
    }

    public ProcessorContext addProcessor(Processor processor) {
        if (null == this.processorList) {
            this.processorList = Lists.newLinkedList();
        }
        this.processorList.add(processor);
        return this;
    }

    public ProcessorContext addProcessor(List<Processor> processorList) {
        if (null == this.processorList) {
            this.processorList = processorList;
            return this;
        }
        this.processorList.addAll(processorList);
        return null;
    }

    public ProcessorContext addConnection(BiEtlConnection connection) {
        if (null == this.connectionList) {
            this.connectionList = Lists.newLinkedList();
        }
        this.connectionList.add(connection);
        return this;
    }

    public ProcessorContext addConnection(List<BiEtlConnection> connectionList) {
        if (null == this.connectionList) {
            this.connectionList = connectionList;
            return this;
        }
        this.connectionList.addAll(connectionList);
        return this;
    }
}