package com.deloitte.bdh.data.collation.nifi.dto;

import com.deloitte.bdh.data.collation.enums.ProcessorTypeEnum;
import com.deloitte.bdh.data.collation.model.BiEtlConnection;
import com.deloitte.bdh.data.collation.model.BiEtlDatabaseInf;
import com.deloitte.bdh.data.collation.model.BiProcessors;
import com.google.common.collect.Lists;
import lombok.*;

import java.util.List;


@Data
public class ProcessorContext extends Nifi {
    private List<ProcessorTypeEnum> enumList;

    private BiEtlDatabaseInf biEtlDatabaseInf = null;
    private BiProcessors processors = null;

    private List<Processor> processorList = null;
    private Processor tempProcessor = null;

    private List<BiEtlConnection> connectionList = null;
    private BiEtlConnection tempConnection = null;

    private Integer processorSequ = null;

    //删除才有
    private List<Processor> hasDelProcessorList = Lists.newLinkedList();
    private List<Processor> newProcessorList = Lists.newLinkedList();
    private List<BiEtlConnection> hasDelConnectionList = Lists.newLinkedList();

    public ProcessorContext addProcessorTemp(Processor processor) {
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

    public ProcessorContext addProcessorList(Processor processor) {
        if (null == this.processorList) {
            this.processorList = Lists.newLinkedList();
        }
        this.processorList.add(processor);
        return this;
    }

    public ProcessorContext addProcessorList(List<Processor> processorList) {
        if (null == this.processorList) {
            this.processorList = processorList;
            return this;
        }
        this.processorList.addAll(processorList);
        return null;
    }

    public ProcessorContext addConnectionList(BiEtlConnection connection) {
        if (null == this.connectionList) {
            this.connectionList = Lists.newLinkedList();
        }
        this.connectionList.add(connection);
        return this;
    }

    public ProcessorContext addConnectionList(List<BiEtlConnection> connectionList) {
        if (null == this.connectionList) {
            this.connectionList = connectionList;
            return this;
        }
        this.connectionList.addAll(connectionList);
        return this;
    }
}
