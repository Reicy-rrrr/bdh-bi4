package com.deloitte.bdh.data.collation.nifi.dto;

import com.deloitte.bdh.data.collation.model.BiEtlModel;
import com.deloitte.bdh.data.collation.nifi.enums.MethodEnum;
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
