package com.deloitte.bdh.data.collation.integration.impl;

import com.deloitte.bdh.common.http.HttpClientUtil;
import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.common.util.NifiProcessUtil;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.collation.enums.EffectEnum;
import com.deloitte.bdh.data.collation.enums.NifiEnum;
import com.deloitte.bdh.data.collation.nifi.exception.NifiException;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.codehaus.jackson.type.TypeReference;

import java.util.List;
import java.util.Map;


@Service
public class NifiProcessServiceImpl extends AbstractNifiProcess {
    private static final Logger logger = LoggerFactory.getLogger(NifiProcessServiceImpl.class);

    @Override
    public Map<String, Object> cluster() throws Exception {
        logger.info("NifiProcessServiceImpl.cluster, URL:{}", URL + NifiEnum.ACCESS_TOKEN.getKey());
        String response = HttpClientUtil.get(URL + NifiEnum.NIFI_CLUSTER.getKey(), super.setHeaderAuthorization(), null);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> getRootGroupInfo() throws Exception {
        logger.info("NifiProcessServiceImpl.getRootGroupInfo, URL:{} ", URL + NifiEnum.ACCESS_TOKEN.getKey());
        String response = HttpClientUtil.get(NifiProcessUtil.assemblyUrl(URL, NifiEnum.ROOT_GROUP_INFO.getKey()), super.setHeaderAuthorization(), null);
        if (StringUtil.isEmpty(response)) {
            throw new NifiException("未获取到NIFI的RootGroup相关信息");
        }
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> createProcessGroup(Map<String, Object> map, String id) throws Exception {
        //id为空取rootGroup
        if (StringUtil.isEmpty(id)) {
            Map<String, Object> rootGroupInfos = this.getRootGroupInfo();
            // 校验权限
            NifiProcessUtil.checkPermissions(rootGroupInfos);
            Map processGroupFlowMap = MapUtils.getMap(rootGroupInfos, "processGroupFlow");
            id = MapUtils.getString(processGroupFlowMap, "id");
        }

        //请求参数设置
        Map<String, Object> req = NifiProcessUtil.postParam(map);
        String url = NifiProcessUtil.assemblyUrl(URL, NifiEnum.CREATE_PROCSS_GROUP.getKey(), id);
        logger.info("NifiProcessServiceImpl.createProcessGroup, URL:{} ,REQUEST:{}", url, JsonUtil.obj2String(req));
        String response = HttpClientUtil.post(url, super.setHeaderAuthorization(), req);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> getProcessGroup(String id) throws Exception {
        if (StringUtil.isEmpty(id)) {
            throw new NifiException("查询单个ProcessGroup 失败:id不能为空");
        }
        String url = NifiProcessUtil.assemblyUrl(URL, NifiEnum.PROCSS_GROUPS.getKey(), id);
        logger.info("NifiProcessServiceImpl.getProcessGroup, URL:{}", url);
        String response = HttpClientUtil.get(url, super.setHeaderAuthorization(), null);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> getProcessGroupFull(String id) throws Exception {
        if (StringUtil.isEmpty(id)) {
            throw new NifiException("查询单个ProcessGroup 失败:id不能为空");
        }
        String url = NifiProcessUtil.assemblyUrl(URL, NifiEnum.RUN_PROCESSGROUP.getKey(), id);
        logger.info("NifiProcessServiceImpl.getProcessGroup, URL:{}", url);
        String response = HttpClientUtil.get(url, super.setHeaderAuthorization(), null);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> delProcessGroup(String id) throws Exception {
        if (StringUtil.isEmpty(id)) {
            throw new NifiException("delProcessGroup:id不能为空");
        }
        Map<String, Object> sourceMap = this.getProcessGroup(id);
        // 校验权限
        NifiProcessUtil.checkPermissions(sourceMap);
        String url = NifiProcessUtil.assemblyUrl(URL, NifiEnum.PROCSS_GROUPS.getKey(), id);

        Map<String, Object> headers = super.setHeaderAuthorization();
        url = url + "?version=" + MapUtils.getMap(sourceMap, "revision").get("version");
        logger.info("NifiProcessServiceImpl.delProcessGroup 信息, URL:{} ", url);

        String response = HttpClientUtil.delete(url, headers);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> updProcessGroup(Map<String, Object> map) throws Exception {
        NifiProcessUtil.validateRequestMap(map, "id");
        Map<String, Object> sourceMap = this.getProcessGroup(MapUtils.getString(map, "id"));
        // 校验权限
        NifiProcessUtil.checkPermissions(sourceMap);

        //请求参数设置
        Map<String, Object> req = NifiProcessUtil.postParam(map, (Map<String, Object>) MapUtils.getMap(sourceMap, "revision"));
        String url = NifiProcessUtil.assemblyUrl(URL, NifiEnum.PROCSS_GROUPS.getKey(), MapUtils.getString(map, "id"));
        logger.info("NifiProcessServiceImpl.updProcessGroup, URL:{} ,REQUEST:{}", url, JsonUtil.obj2String(req));
        String response = HttpClientUtil.put(url, super.setHeaderAuthorization(), req);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> runState(String id, String state, boolean isGroup) throws Exception {
        if (StringUtil.isEmpty(id) || StringUtil.isEmpty(state)) {
            throw new NifiException("runState 失败 : 参数不能为空");
        }
        Map<String, Object> prcessorMap = null;
        String url;
        if (isGroup) {
            prcessorMap = this.getProcessGroup(id);
            url = NifiProcessUtil.assemblyUrl(URL, NifiEnum.RUN_PROCESSGROUP.getKey(), id);
        } else {
            prcessorMap = this.getProcessor(id);
            url = NifiProcessUtil.assemblyUrl(URL, NifiEnum.RUN_PROCESSOR.getKey(), id);
        }

        // 校验权限
        NifiProcessUtil.checkPermissions(prcessorMap);

        //请求参数设置
        Map<String, Object> req = NifiProcessUtil.postParam(null, (Map<String, Object>) MapUtils.getMap(prcessorMap, "revision"));
        req.put("state", state);
        if (isGroup) {
            req.put("id", id);
            req.remove("revision");
        }
        logger.info("NifiProcessServiceImpl.runState, URL:{} ,REQUEST:{}", url, JsonUtil.obj2String(req));
        String response = HttpClientUtil.put(url, super.setHeaderAuthorization(), req);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> clearRequest(String processorsId) throws Exception {
        if (StringUtil.isEmpty(processorsId)) {
            throw new NifiException("clearRequest 失败 : 参数不能为空");
        }
        String url = NifiProcessUtil.assemblyUrl(URL, NifiEnum.CLEAR_REQUEST.getKey(), processorsId);
        logger.info("NifiProcessServiceImpl.clearRequest, URL:{} ,REQUEST:{}", url, null);
        String response = HttpClientUtil.post(url, super.setHeaderAuthorization(), null);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> terminate(String processorId) throws Exception {
        if (StringUtil.isEmpty(processorId)) {
            throw new NifiException("terminate 失败 : 参数不能为空");
        }
        String url = NifiProcessUtil.assemblyUrl(URL, NifiEnum.TERMINATE.getKey(), processorId);
        //请求参数设置
        logger.info("NifiProcessServiceImpl.terminate, URL:{} ", url);
        String response = HttpClientUtil.delete(url, super.setHeaderAuthorization());
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> getMax(String processorsId) throws Exception {
        if (StringUtil.isEmpty(processorsId)) {
            throw new NifiException("NifiProcessServiceImpl.getMax 失败 : 参数不能为空");
        }
        String url = NifiProcessUtil.assemblyUrl(URL, NifiEnum.GET_MAX.getKey(), processorsId);
        logger.info("NifiProcessServiceImpl.getMax, URL:{} ", url);
        String response = HttpClientUtil.get(url, super.setHeaderAuthorization(), null);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> createControllerService(Map<String, Object> map) throws Exception {
        NifiProcessUtil.validateRequestMap(map, "type", "name", "dbUser", "passWord", "dbUrl", "driverName", "driverLocations");

        String id = MapUtils.getString(map, "id");
        //数据源创建默认scope为rootGroup的Id
        if (StringUtil.isEmpty(id)) {
            Map<String, Object> rootGroupInfos = this.getRootGroupInfo();
            //权限校验
            NifiProcessUtil.checkPermissions(rootGroupInfos);
            Map processGroupFlowMap = MapUtils.getMap(rootGroupInfos, "processGroupFlow");
            id = MapUtils.getString(processGroupFlowMap, "id");
        }

        Map<String, Object> param = Maps.newHashMap();
        //连接池类型
        param.put("type", MapUtils.getString(map, "type"));
        param.put("name", MapUtils.getString(map, "name"));
        param.put("comments", MapUtils.getString(map, "comments"));

        Map<String, Object> properties = Maps.newHashMap();
        properties.put("Database User", MapUtils.getString(map, "dbUser"));
        properties.put("Password", MapUtils.getString(map, "passWord"));
        properties.put("Database Connection URL", MapUtils.getString(map, "dbUrl"));
        properties.put("Database Driver Class Name", MapUtils.getString(map, "driverName"));
        properties.put("database-driver-locations", MapUtils.getString(map, "driverLocations"));
        param.put("properties", properties);

        Map<String, Object> req = NifiProcessUtil.postParam(param);
        String url = NifiProcessUtil.assemblyUrl(URL, NifiEnum.CREATE_CONTROLLER_SERVICE.getKey(), id);
        logger.info("NifiProcessServiceImpl.createControllerService, URL:{} ,REQUEST:{}", url, JsonUtil.obj2String(req));
        String response = HttpClientUtil.post(url, super.setHeaderAuthorization(), req);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> createOtherControllerService(Map<String, Object> map) throws Exception {
        NifiProcessUtil.validateRequestMap(map, "type", "name");

        String id = MapUtils.getString(map, "id");
        // 数据源创建默认scope为rootGroup的Id
        if (StringUtil.isEmpty(id)) {
            Map<String, Object> rootGroupInfos = this.getRootGroupInfo();
            //权限校验
            NifiProcessUtil.checkPermissions(rootGroupInfos);
            Map processGroupFlowMap = MapUtils.getMap(rootGroupInfos, "processGroupFlow");
            id = MapUtils.getString(processGroupFlowMap, "id");
        }

        Map<String, Object> param = Maps.newHashMap();
        // 连接池类型
        param.put("type", MapUtils.getString(map, "type"));
        param.put("name", MapUtils.getString(map, "name"));
        param.put("comments", MapUtils.getString(map, "comments"));

        Map<String, Object> properties = Maps.newHashMap();
        properties.putAll(map);
        properties.remove("type");
        properties.remove("name");
        properties.remove("comments");
        param.put("properties", properties);

        Map<String, Object> req = NifiProcessUtil.postParam(param);
        String url = NifiProcessUtil.assemblyUrl(URL, NifiEnum.CREATE_CONTROLLER_SERVICE.getKey(), id);
        logger.info("NifiProcessServiceImpl.createControllerService, URL:{} ,REQUEST:{}", url, JsonUtil.obj2String(req));
        String response = HttpClientUtil.post(url, super.setHeaderAuthorization(), req);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> runControllerService(String id, String state) throws Exception {
        if (StringUtil.isEmpty(id) || StringUtil.isEmpty(state)) {
            throw new NifiException("runControllerService 失败:参数不能为空");
        }
        Map<String, Object> prcessorMap = this.getControllerService(id);

        // 校验权限
        NifiProcessUtil.checkPermissions(prcessorMap);
        //请求参数设置
        Map<String, Object> req = NifiProcessUtil.postParam(null, (Map<String, Object>) MapUtils.getMap(prcessorMap, "revision"));
        String controllerServiceState = EffectEnum.ENABLE.getKey().equals(state) ? "ENABLED" : "DISABLED";
        req.put("state", controllerServiceState);
        String url = NifiProcessUtil.assemblyUrl(URL, NifiEnum.RUN_CONTROLLER_SERVICE.getKey(), id);
        logger.info("NifiProcessServiceImpl.runControllerService, URL:{} ,REQUEST:{}", url, JsonUtil.obj2String(req));
        String response = HttpClientUtil.put(url, super.setHeaderAuthorization(), req);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> getControllerService(String id) throws Exception {
        if (StringUtil.isEmpty(id)) {
            throw new NifiException("查询单个数据源失败:id不能为空");
        }

        String url = NifiProcessUtil.assemblyUrl(URL, NifiEnum.CONTROLLER_SERVICE.getKey(), id);
        logger.info("NifiProcessServiceImpl.getControllerService 信息, URL:{}", url);
        String response = HttpClientUtil.get(url, super.setHeaderAuthorization(), null);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> delControllerService(String id) throws Exception {
        if (StringUtil.isEmpty(id)) {
            throw new NifiException("delControllerService:id不能为空");
        }
        Map<String, Object> connectMap = this.getControllerService(id);
        // 校验权限
        NifiProcessUtil.checkPermissions(connectMap);
        String url = NifiProcessUtil.assemblyUrl(URL, NifiEnum.CONTROLLER_SERVICE.getKey(), id);

        Map<String, Object> headers = super.setHeaderAuthorization();
        url = url + "?version=" + MapUtils.getMap(connectMap, "revision").get("version");
        logger.info("NifiProcessServiceImpl.delControllerService 信息, URL:{} ", url);

        String response = HttpClientUtil.delete(url, headers);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> updControllerService(Map<String, Object> map) throws Exception {
        NifiProcessUtil.validateRequestMap(map, "id");
        Map<String, Object> prcessorMap = this.getControllerService(MapUtils.getString(map, "id"));
        // 校验权限
        NifiProcessUtil.checkPermissions(prcessorMap);

        //请求参数设置
        Map<String, Object> req = NifiProcessUtil.postParam(map, (Map<String, Object>) MapUtils.getMap(prcessorMap, "revision"));
        String url = NifiProcessUtil.assemblyUrl(URL, NifiEnum.CONTROLLER_SERVICE.getKey(), MapUtils.getString(map, "id"));
        logger.info("NifiProcessServiceImpl.updControllerService, URL:{} ,REQUEST:{}", url, JsonUtil.obj2String(req));
        String response = HttpClientUtil.put(url, super.setHeaderAuthorization(), req);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> createProcessor(Map<String, Object> map) throws Exception {
        NifiProcessUtil.validateRequestMap(map, "id", "type");
        String id = MapUtils.getString(map, "id");
        // 校验权限
        NifiProcessUtil.checkPermissions(this.getProcessGroup(id));
        //删除id
        map.remove("id");
        Map<String, Object> req = NifiProcessUtil.postParam(map);
        String url = NifiProcessUtil.assemblyUrl(URL, NifiEnum.CREATE_PROCESSOR.getKey(), id);
        logger.info("NifiProcessServiceImpl.createProcessor, URL:{} ,REQUEST:{}", url, JsonUtil.obj2String(req));
        String response = HttpClientUtil.post(url, super.setHeaderAuthorization(), req);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> getProcessor(String id) throws Exception {
        if (StringUtil.isEmpty(id)) {
            throw new NifiException("查询单个Processor失败:id不能为空");
        }

        String url = NifiProcessUtil.assemblyUrl(URL, NifiEnum.PROCESSORS.getKey(), id);
        logger.info("NifiProcessServiceImpl.getProcessor 信息, URL:{} ", url);
        String response = HttpClientUtil.get(url, super.setHeaderAuthorization(), null);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> updateProcessor(Map<String, Object> map) throws Exception {
        NifiProcessUtil.validateRequestMap(map, "id");

        //processor id
        String id = MapUtils.getString(map, "id");
        Map<String, Object> prcessorMap = this.getProcessor(id);
        // 校验权限
        NifiProcessUtil.checkPermissions(prcessorMap);
        //请求参数设置
        Map<String, Object> req = NifiProcessUtil.postParam(map, (Map<String, Object>) MapUtils.getMap(prcessorMap, "revision"));
        String url = NifiProcessUtil.assemblyUrl(URL, NifiEnum.PROCESSORS.getKey(), id);
        logger.info("NifiProcessServiceImpl.updateProcessor, URL:{} ,REQUEST:{}", url, JsonUtil.obj2String(req));
        String response = HttpClientUtil.put(url, super.setHeaderAuthorization(), req);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> delProcessor(String id) throws Exception {
        if (StringUtil.isEmpty(id)) {
            throw new NifiException("NifiProcessServiceImpl.delProcessor:id不能为空");
        }
        Map<String, Object> processor = this.getProcessor(id);
        // 校验权限
        NifiProcessUtil.checkPermissions(processor);
        String url = NifiProcessUtil.assemblyUrl(URL, NifiEnum.PROCESSORS.getKey(), id);

        Map<String, Object> headers = super.setHeaderAuthorization();
        url = url + "?version=" + MapUtils.getMap(processor, "revision").get("version");
        logger.info("NifiProcessServiceImpl.delProcessor 信息, URL:{} ", url);

        String response = HttpClientUtil.delete(url, headers);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> createConnections(Map<String, Object> map, String id) throws Exception {
        if (StringUtil.isEmpty(id)) {
            throw new NifiException("createConnections error: id不能为空");
        }
        Map<String, Object> prcessorGroupMap = this.getProcessGroup(id);
        // 校验权限
        NifiProcessUtil.checkPermissions(prcessorGroupMap);
        //请求参数设置
        Map<String, Object> req = NifiProcessUtil.postParam(map);
        String url = NifiProcessUtil.assemblyUrl(URL, NifiEnum.CREATE_CONNECTIONS.getKey(), id);
        logger.info("NifiProcessServiceImpl.createConnections, URL:{} ,REQUEST:{}", url, JsonUtil.obj2String(req));
        String response = HttpClientUtil.post(url, super.setHeaderAuthorization(), req);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> getConnections(String id) throws Exception {
        if (StringUtil.isEmpty(id)) {
            throw new NifiException("getConnections失败:id不能为空");
        }

        String url = NifiProcessUtil.assemblyUrl(URL, NifiEnum.CONNECTIONS.getKey(), id);
        logger.info("NifiProcessServiceImpl.getConnections 信息, URL:{} ", url);
        String response = HttpClientUtil.get(url, super.setHeaderAuthorization(), null);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> dropConnections(String id) throws Exception {
        if (StringUtil.isEmpty(id)) {
            throw new NifiException("dropConnections失败:id不能为空");
        }

        String url = NifiProcessUtil.assemblyUrl(URL, NifiEnum.DROP_CONNECTIONS.getKey(), id);
        logger.info("NifiProcessServiceImpl.dropConnections 信息, URL:{} ", url);
        String response = HttpClientUtil.post(url, super.setHeaderAuthorization(), null);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> delConnections(String id) throws Exception {
        if (StringUtil.isEmpty(id)) {
            throw new NifiException("delConnections:id不能为空");
        }
        Map<String, Object> connectMap = this.getConnections(id);
        // 校验权限
        NifiProcessUtil.checkPermissions(connectMap);
        String url = NifiProcessUtil.assemblyUrl(URL, NifiEnum.CONNECTIONS.getKey(), id);

        Map<String, Object> headers = super.setHeaderAuthorization();
        url = url + "?version=" + MapUtils.getMap(connectMap, "revision").get("version");
        logger.info("NifiProcessServiceImpl.delConnections 信息, URL:{} ", url);

        String response = HttpClientUtil.delete(url, headers);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> getListingRequest(String connectionId) throws Exception {
        if (StringUtil.isEmpty(connectionId)) {
            throw new NifiException("NifiProcessServiceImpl.getListing 失败:connectionId 不能为空");
        }

        String url = NifiProcessUtil.assemblyUrl(URL, NifiEnum.LISTING_REQUESTS.getKey(), connectionId);
        logger.info("NifiProcessServiceImpl.getListing 信息, URL:{} ", url);
        String response = HttpClientUtil.post(url, super.setHeaderAuthorization(), null);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> getFlowFileList(String connectionId, String requestId) throws Exception {
        if (StringUtil.isEmpty(connectionId) || StringUtil.isEmpty(requestId)) {
            throw new NifiException("NifiProcessServiceImpl.getFlowFileList 失败:id 不能为空");
        }

        String url = NifiProcessUtil.assemblyUrl(URL, NifiEnum.LISTING_FLOWFILE_IDS.getKey(), connectionId, requestId);
        logger.info("NifiProcessServiceImpl.getFlowFileList 信息, URL:{} ", url);
        String response = HttpClientUtil.get(url, super.setHeaderAuthorization(), null);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public String getFlowFileContent(String connectionId, String flowFileId, String clusterNodeId) throws Exception {
        if (StringUtil.isEmpty(connectionId) || StringUtil.isEmpty(flowFileId) || StringUtil.isEmpty(clusterNodeId)) {
            throw new NifiException("NifiProcessServiceImpl.getFlowFileContent 失败:id 不能为空");
        }

        String url = NifiProcessUtil.assemblyUrl(URL, NifiEnum.LISTING_FLOWFILE_CONTENT.getKey(), connectionId, flowFileId);
        url = url + "?clusterNodeId=" + clusterNodeId;
        logger.info("NifiProcessServiceImpl.getFlowFileContent 信息, URL:{} ", url);
        return HttpClientUtil.get(url, super.setHeaderAuthorization(), null);
    }

    @Override
    public String preview(String connectionId) throws Exception {
        Map<String, Object> listRequestMap = JsonUtil.string2Obj(
                JsonUtil.obj2String(this.getListingRequest(connectionId).get("listingRequest")),
                new TypeReference<Map<String, Object>>() {
                });

        Integer objectCount = MapUtils.getInteger(JsonUtil.string2Obj(
                JsonUtil.obj2String(listRequestMap.get("queueSize")), new TypeReference<Map<String, Object>>() {
                }), "objectCount");

        if (objectCount == 0) {
            throw new NifiException("NifiProcessServiceImpl.preview error : 数据暂未生成");
        }

        listRequestMap = JsonUtil.string2Obj(
                JsonUtil.obj2String(this.getFlowFileList(connectionId, MapUtils.getString(listRequestMap, "id"))
                        .get("listingRequest")), new TypeReference<Map<String, Object>>() {
                });

        List<Map<String, Object>> flowFileSummaries = JsonUtil.string2Obj(
                JsonUtil.obj2String(listRequestMap.get("flowFileSummaries")),
                new TypeReference<List<Map<String, Object>>>() {
                });

        if (CollectionUtils.isEmpty(flowFileSummaries)) {
            throw new NifiException("NifiProcessServiceImpl.preview error : 数据暂未生成");
        }

        //todo 目前只读取第一个 ，待确定传输文件大小
        Integer size = MapUtils.getInteger(flowFileSummaries.get(0), "size");
        if (size == 0) {
            throw new NifiException("NifiProcessServiceImpl.preview error : 数据暂未生成");
        }
        String uuid = MapUtils.getString(flowFileSummaries.get(0), "uuid");
        String clusterNodeId = MapUtils.getString(flowFileSummaries.get(0), "clusterNodeId");
        return getFlowFileContent(connectionId, uuid, clusterNodeId);
    }


}
