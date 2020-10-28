package com.deloitte.bdh.data.collation.nifi;

import com.deloitte.bdh.data.collation.nifi.dto.ConnectionsContext;
import com.deloitte.bdh.data.collation.nifi.dto.ProcessorContext;

public interface EtlProcess {

    ProcessorContext operateProcessorGroup(ProcessorContext var) throws Exception;

    @Deprecated
    ConnectionsContext operateProcessorGroupConnections(ConnectionsContext var) throws Exception;


}
