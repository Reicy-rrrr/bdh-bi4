package com.deloitte.bdh.data.integration.impl;

import com.deloitte.bdh.common.http.HttpClientUtil;
import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.common.util.NifiProcessUtil;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.enums.NifiEnum;
import com.deloitte.bdh.data.enums.ProcessorEnum;
import com.google.common.collect.Maps;
import com.google.common.collect.ObjectArrays;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class NifiProcessServiceImpl extends AbstractNifiProcess {
    private static final Logger logger = LoggerFactory.getLogger(NifiProcessServiceImpl.class);

    @Override
    public Map<String, Object> cluster() throws Exception {
        logger.info("NifiProcessServiceImpl.cluster, URL:{} ", URL + NifiEnum.ACCESS_TOKEN.getKey());
        String response = HttpClientUtil.httpGetRequest(URL + NifiEnum.NIFI_CLUSTER.getKey(), super.setHeaderAuthorization(), null);
        return JsonUtil.string2Obj(response, Map.class);
    }

    @Override
    public Map<String, Object> getRootGroupInfo() throws Exception {
        logger.info("NifiProcessServiceImpl.getRootGroupInfo, URL:{} ", URL + NifiEnum.ACCESS_TOKEN.getKey());
        String response = HttpClientUtil.httpGetRequest(NifiProcessUtil.assemblyUrl(URL, NifiEnum.ROOT_GROUP_INFO.getKey()), super.setHeaderAuthorization(), null);
        if (StringUtil.isEmpty(response)) {
            throw new RuntimeException("未获取到NIFI的RootGroup相关信息");
        }
        return JsonUtil.string2Obj(response, Map.class);
    }

    @Override
    public Map<String, Object> createProcessGroup(Map<String, Object> map) throws Exception {
        NifiProcessUtil.validateRequestMap(map, "name", "x", "y");

        String id = MapUtils.getString(map, "id");
        if (StringUtil.isEmpty(id)) {
            Map<String, Object> rootGroupInfos = this.getRootGroupInfo();
            // 校验权限
            NifiProcessUtil.checkPermissions(rootGroupInfos);
            Map processGroupFlowMap = MapUtils.getMap(rootGroupInfos, "processGroupFlow");
            id = MapUtils.getString(processGroupFlowMap, "id");
        }

        Map<String, Object> param = Maps.newHashMap();
        param.put("name", MapUtils.getString(map, "name"));
        //请求参数设置
        param = NifiProcessUtil.position(param, map);
        Map<String, Object> req = NifiProcessUtil.postParam(param);
        String url = NifiProcessUtil.assemblyUrl(URL, NifiEnum.CREATE_PROCSS_GROUP.getKey(), id);
        logger.info("NifiProcessServiceImpl.createProcessGroup, URL:{} ,REQUEST:{}", url, JsonUtil.obj2String(req));
        String response = HttpClientUtil.httpPostRequestByJson(url, super.setHeaderAuthorization(), req);
        return JsonUtil.string2Obj(response, Map.class);
    }

    @Override
    public Map<String, Object> getProcessGroup(String id) throws Exception {
        if (StringUtil.isEmpty(id)) {
            throw new RuntimeException("查询单个ProcessGroup 失败:id不能为空");
        }
        String url = NifiProcessUtil.assemblyUrl(URL, NifiEnum.GET_PROCSS_GROUP.getKey(), id);
        logger.info("NifiProcessServiceImpl.getProcessGroup, URL:{} ", url);
        String response = HttpClientUtil.httpGetRequest(url, super.setHeaderAuthorization(), null);
        return JsonUtil.string2Obj(response, Map.class);
    }

    @Override
    public Map<String, Object> createControllerService(Map<String, Object> map) throws Exception {
        NifiProcessUtil.validateRequestMap(map, "name", "dbUser", "passWord", "dbUrl", "driverName");

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
        param.put("type", "org.apache.nifi.dbcp.DBCPConnectionPool");
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
        String response = HttpClientUtil.httpPostRequestByJson(url, super.setHeaderAuthorization(), req);
        return JsonUtil.string2Obj(response, Map.class);
    }

    @Override
    public Map<String, Object> getControllerService(String id) throws Exception {
        if (StringUtil.isEmpty(id)) {
            throw new RuntimeException("查询单个数据源失败:id不能为空");
        }

        String url = NifiProcessUtil.assemblyUrl(URL, NifiEnum.GET_CONTROLLER_SERVICE.getKey(), id);
        logger.info("NifiProcessServiceImpl.getControllerService 信息, URL:{} ", url);
        String response = HttpClientUtil.httpGetRequest(url, super.setHeaderAuthorization(), null);
        return JsonUtil.string2Obj(response, Map.class);
    }

    @Override
    public Map<String, Object> createProcessor(Map<String, Object> map) throws Exception {
        NifiProcessUtil.validateRequestMap(map, "id", "name", "x", "y", "type");

        //processGroup id
        String id = MapUtils.getString(map, "id");

        // 校验权限
        NifiProcessUtil.checkPermissions(this.getProcessGroup(id));

        Map<String, Object> param = Maps.newHashMap();
        param.put("name", MapUtils.getString(map, "name"));
        param.put("type", ProcessorEnum.getValue(MapUtils.getString(map, "type")));

        //请求参数设置
        param = NifiProcessUtil.position(param, map);
        Map<String, Object> req = NifiProcessUtil.postParam(param);
        String url = NifiProcessUtil.assemblyUrl(URL, NifiEnum.CREATE_PROCESSOR.getKey(), id);
        logger.info("NifiProcessServiceImpl.createProcessor, URL:{} ,REQUEST:{}", url, JsonUtil.obj2String(req));
        String response = HttpClientUtil.httpPostRequestByJson(url, super.setHeaderAuthorization(), req);
        return JsonUtil.string2Obj(response, Map.class);
    }

    @Override
    public Map<String, Object> getProcessor(String id) throws Exception {
        if (StringUtil.isEmpty(id)) {
            throw new RuntimeException("查询单个Processor失败:id不能为空");
        }

        String url = NifiProcessUtil.assemblyUrl(URL, NifiEnum.PROCESSORS.getKey(), id);
        logger.info("NifiProcessServiceImpl.getProcessor 信息, URL:{} ", url);
        String response = HttpClientUtil.httpGetRequest(url, super.setHeaderAuthorization(), null);
        return JsonUtil.string2Obj(response, Map.class);
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
        String response = HttpClientUtil.httpPutRequestByJson(url, super.setHeaderAuthorization(), req);
        return JsonUtil.string2Obj(response, Map.class);
    }


}
