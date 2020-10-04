package com.deloitte.bdh.data.nifi;

import com.deloitte.bdh.data.model.BiConnections;
import com.deloitte.bdh.data.model.BiEtlConnection;
import com.deloitte.bdh.data.model.BiProcessors;
import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;


@Data
public class ConnectionsContext extends Nifi {

    //增加时候
    private List<BiProcessors> fromProcessorsList = Lists.newLinkedList();
    private List<BiProcessors> toProcessorsList = Lists.newLinkedList();

    //公共
    private List<BiConnections> connectionsList = Lists.newLinkedList();
    private List<BiEtlConnection> connectionList = Lists.newLinkedList();

    //删除时候
    private List<BiConnections> hasConnectionsList = Lists.newLinkedList();
    private List<BiEtlConnection> hasConnectionList = Lists.newLinkedList();
}
