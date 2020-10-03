package com.deloitte.bdh.data.nifi;

import com.deloitte.bdh.data.model.BiEtlModel;
import com.deloitte.bdh.data.model.BiProcessors;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;

import java.util.List;
import java.util.Map;


@Data
public class ConnectionsContext extends Nifi {
    private Map<String, Object> req = Maps.newHashMap();

    private BiEtlModel model = new BiEtlModel();
    private List<BiProcessors> fromProcessorsList = Lists.newLinkedList();
    private List<BiProcessors> toProcessorsList = Lists.newLinkedList();


}
