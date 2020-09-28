package com.deloitte.bdh.data.nifi.connection;

import com.deloitte.bdh.data.nifi.ProcessorContext;

import java.util.Map;

public interface Connection {

    Map<String, Object> pConnect(ProcessorContext context);

    Map<String, Object> rConnect(ProcessorContext context);

}
