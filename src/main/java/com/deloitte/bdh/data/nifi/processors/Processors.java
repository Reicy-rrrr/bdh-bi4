package com.deloitte.bdh.data.nifi.processors;

import com.deloitte.bdh.data.nifi.ProcessorContext;

public interface Processors {

    ProcessorContext etl(ProcessorContext context) throws Exception;
}
