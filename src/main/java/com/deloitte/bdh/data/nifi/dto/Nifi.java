package com.deloitte.bdh.data.nifi.dto;

import com.deloitte.bdh.data.model.BiEtlModel;
import com.deloitte.bdh.data.nifi.enums.MethodEnum;
import com.google.common.collect.Maps;
import lombok.Data;

import java.util.Map;

@Data
public class Nifi {
    //不能为空
    protected MethodEnum method;

    protected Map<String, Object> req = Maps.newHashMap();

    //不能为空
    protected BiEtlModel model = null;

    protected String result;
}
