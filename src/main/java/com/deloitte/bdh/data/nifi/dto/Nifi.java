package com.deloitte.bdh.data.nifi.dto;

import com.deloitte.bdh.data.model.BiEtlModel;
import com.deloitte.bdh.data.nifi.enums.MethodEnum;
import com.google.common.collect.Maps;
import lombok.Data;

import java.util.Map;

@Data
public class Nifi {

    protected MethodEnum method;
    protected Map<String, Object> req = Maps.newHashMap();
    protected BiEtlModel model = null;
}
