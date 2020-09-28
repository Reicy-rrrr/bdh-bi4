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

    private List<Map<String, Object>> successProcessMap = Lists.newArrayList();
    private Boolean processComplete = false;

    private List<Map<String, Object>> successConnectionMap;
    private Boolean connectionComplete = false;


}
