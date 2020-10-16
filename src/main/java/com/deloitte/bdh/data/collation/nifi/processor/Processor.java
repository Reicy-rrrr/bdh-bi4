package com.deloitte.bdh.data.collation.nifi.processor;


import com.deloitte.bdh.data.collation.nifi.dto.ProcessorContext;

import java.util.Map;

public interface Processor {

    Map<String, Object> pProcess(ProcessorContext context) throws Exception;

    Map<String, Object> rProcess(ProcessorContext context) throws Exception;

}
