package com.deloitte.bdh.data.nifi;

import com.deloitte.bdh.data.enums.ProcessorTypeEnum;
import com.deloitte.bdh.data.model.BiEtlDatabaseInf;
import com.deloitte.bdh.data.model.BiEtlModel;
import com.deloitte.bdh.data.model.BiProcessors;
import com.deloitte.bdh.data.model.resp.Processor;
import com.google.common.collect.Lists;
import lombok.*;

import java.util.List;
import java.util.Map;


@Data
public class ProcessorContext {
    private List<ProcessorTypeEnum> enumList;
    private Map<String, Object> req;
    private MethodEnum method;

    private BiEtlModel model = new BiEtlModel();
    private BiEtlDatabaseInf biEtlDatabaseInf = new BiEtlDatabaseInf();
    private BiProcessors processors = new BiProcessors();

    private List<Processor> processorList = Lists.newArrayList();
    private Processor tempProcessor = null;


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
        return this;
    }
}
