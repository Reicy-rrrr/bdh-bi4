package com.deloitte.bdh.data.collation.nifi.template.servie.impl;


import com.beust.jcommander.internal.Lists;
import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.data.collation.nifi.template.servie.Transfer;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public abstract class AbstractTransfer implements Transfer {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractTransfer.class);


    protected String parseGroupId(Map<String, Object> templateResp) {
        Map<String, Object> flow = JsonUtil.string2Obj(
                JsonUtil.obj2String(templateResp.get("flow")), new TypeReference<Map<String, Object>>() {
                });

        Map<String, Object> processGroups = JsonUtil.string2Obj(
                JsonUtil.obj2String(flow.get("processGroups")), new TypeReference<List<Map<String, Object>>>() {
                }).get(0);

        return MapUtils.getString(processGroups, "id");
    }

    protected Map<String, String> parseProcessors(Map<String, Object> processGroup) {
        Map<String, String> processor = Maps.newHashMap();

        Map<String, Object> processGroupFlow = JsonUtil.string2Obj(
                JsonUtil.obj2String(processGroup.get("processGroupFlow")), new TypeReference<Map<String, Object>>() {
                });

        Map<String, Object> flow = JsonUtil.string2Obj(
                JsonUtil.obj2String(processGroupFlow.get("flow")), new TypeReference<Map<String, Object>>() {
                });


        List<Map<String, Object>> processors = JsonUtil.string2Obj(
                JsonUtil.obj2String(flow.get("processors")), new TypeReference<List<Map<String, Object>>>() {
                });


        processors.forEach(s -> {
            Map<String, Object> component = JsonUtil.string2Obj(
                    JsonUtil.obj2String(s.get("component")), new TypeReference<Map<String, Object>>() {
                    });
            processor.put(MapUtils.getString(component, "id"), MapUtils.getString(component, "name"));
        });
        return processor;
    }


    protected List<String> parseConnections(Map<String, Object> processGroup) {
        List<String> list = Lists.newArrayList();
        Map<String, Object> processGroupFlow = JsonUtil.string2Obj(
                JsonUtil.obj2String(processGroup.get("processGroupFlow")), new TypeReference<Map<String, Object>>() {
                });
        Map<String, Object> flow = JsonUtil.string2Obj(
                JsonUtil.obj2String(processGroupFlow.get("flow")), new TypeReference<Map<String, Object>>() {
                });

        List<Map<String, Object>> connections = JsonUtil.string2Obj(
                JsonUtil.obj2String(flow.get("connections")), new TypeReference<List<Map<String, Object>>>() {
                });

        connections.forEach(s -> list.add(MapUtils.getString(s, "id")));
        return list;
    }


}
