package com.deloitte.bdh.data.nifi;

import com.deloitte.bdh.data.enums.ProcessorTypeEnum;
import com.deloitte.bdh.data.model.*;
import com.deloitte.bdh.data.nifi.enums.MethodEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;

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

    private List<BiEtlConnection> connectionList = null;
    private BiEtlConnection tempConnection = null;


    private Boolean processorComplete = false;
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
