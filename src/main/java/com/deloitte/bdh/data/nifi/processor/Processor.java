package com.deloitte.bdh.data.nifi.processor;


import com.deloitte.bdh.data.nifi.dto.ProcessorContext;

import java.util.Map;

public interface Processor {

    Map<String, Object> pProcess(ProcessorContext context) throws Exception;

    Map<String, Object> rProcess(ProcessorContext context) throws Exception;

}
