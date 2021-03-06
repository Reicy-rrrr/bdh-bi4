package com.deloitte.bdh.data.collation.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.http.HttpClientUtil;
import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.common.util.NifiProcessUtil;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.enums.ResourceMessageEnum;
import com.deloitte.bdh.data.analyse.service.impl.LocaleMessageService;
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

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Service
public class NifiProcessServiceImpl extends AbstractNifiProcess {
    private static final Logger logger = LoggerFactory.getLogger(NifiProcessServiceImpl.class);

    @Resource
    private LocaleMessageService localeMessageService;

    @Override
    public Map<String, Object> cluster() throws Exception {
        logger.info("NifiProcessServiceImpl.cluster, URL:{}", biProperties.getNifiUrl() + NifiEnum.ACCESS_TOKEN.getKey());
        String response = HttpClientUtil.get(biProperties.getNifiUrl() + NifiEnum.NIFI_CLUSTER.getKey(), super.setHeaderAuthorization(), null);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> getRootGroupInfo() throws Exception {
        logger.info("NifiProcessServiceImpl.getRootGroupInfo, URL:{} ", biProperties.getNifiUrl() + NifiEnum.ACCESS_TOKEN.getKey());
        String response = HttpClientUtil.get(NifiProcessUtil.assemblyUrl(biProperties.getNifiUrl(), NifiEnum.ROOT_GROUP_INFO.getKey()), super.setHeaderAuthorization(), null);
        if (StringUtil.isEmpty(response)) {
            throw new BizException(ResourceMessageEnum.ROOT_GROUP_ERROR.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.ROOT_GROUP_ERROR.getMessage(), ThreadLocalHolder.getLang()));
        }
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> createProcessGroup(Map<String, Object> map, String id) {
        try {
            //id?????????rootGroup
            if (StringUtil.isEmpty(id)) {
                Map<String, Object> rootGroupInfos = this.getRootGroupInfo();
                // ????????????
                NifiProcessUtil.checkPermissions(rootGroupInfos);
                Map processGroupFlowMap = MapUtils.getMap(rootGroupInfos, "processGroupFlow");
                id = MapUtils.getString(processGroupFlowMap, "id");
            }

            //??????????????????
            Map<String, Object> req = NifiProcessUtil.postParam(map);
            String url = NifiProcessUtil.assemblyUrl(biProperties.getNifiUrl(), NifiEnum.CREATE_PROCSS_GROUP.getKey(), id);
            logger.info("NifiProcessServiceImpl.createProcessGroup, URL:{} ,REQUEST:{}", url, JsonUtil.obj2String(req));
            String response = HttpClientUtil.post(url, super.setHeaderAuthorization(), req);
            return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            logger.error("NifiProcessServiceImpl.createProcessGroup.error:", e);
            throw new BizException(ResourceMessageEnum.SYSTEM_ERROR.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.SYSTEM_ERROR.getMessage(), ThreadLocalHolder.getLang()));
        }
    }

    @Override
    public Map<String, Object> getProcessGroup(String id) throws Exception {
        if (StringUtil.isEmpty(id)) {
            throw new NifiException("NifiProcessServiceImpl.getProcessGroup.error:id????????????");
        }
        String url = NifiProcessUtil.assemblyUrl(biProperties.getNifiUrl(), NifiEnum.PROCSS_GROUPS.getKey(), id);
        logger.info("NifiProcessServiceImpl.getProcessGroup, URL:{}", url);
        String response = HttpClientUtil.get(url, super.setHeaderAuthorization(), null);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> getProcessGroupFull(String id) {
        try {
            if (StringUtil.isEmpty(id)) {
                throw new NifiException("NifiProcessServiceImpl.getProcessGroupFull.error :id????????????");
            }
            String url = NifiProcessUtil.assemblyUrl(biProperties.getNifiUrl(), NifiEnum.RUN_PROCESSGROUP.getKey(), id);
            logger.info("NifiProcessServiceImpl.getProcessGroup, URL:{}", url);
            String response = HttpClientUtil.get(url, super.setHeaderAuthorization(), null);
            return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            logger.error("NifiProcessServiceImpl.getProcessGroupFull.error:", e);
            throw new BizException(ResourceMessageEnum.SYSTEM_ERROR.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.SYSTEM_ERROR.getMessage(), ThreadLocalHolder.getLang()));

        }
    }

    @Override
    public Map<String, Object> delProcessGroup(String id) {
        try {
            if (StringUtil.isEmpty(id)) {
                throw new NifiException("NifiProcessServiceImpl.delProcessGroup.error :id????????????");
            }
            Map<String, Object> sourceMap = this.getProcessGroup(id);
            // ????????????
            NifiProcessUtil.checkPermissions(sourceMap);
            String url = NifiProcessUtil.assemblyUrl(biProperties.getNifiUrl(), NifiEnum.PROCSS_GROUPS.getKey(), id);

            Map<String, Object> headers = super.setHeaderAuthorization();
            url = url + "?version=" + MapUtils.getMap(sourceMap, "revision").get("version");
            logger.info("NifiProcessServiceImpl.delProcessGroup ??????, URL:{} ", url);

            String response = HttpClientUtil.delete(url, headers);
            return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            logger.error("NifiProcessServiceImpl.delProcessGroup.error:", e);
            throw new BizException(ResourceMessageEnum.SYSTEM_ERROR.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.SYSTEM_ERROR.getMessage(), ThreadLocalHolder.getLang()));
        }
    }

    @Override
    public Map<String, Object> updProcessGroup(Map<String, Object> map) {
        try {
            NifiProcessUtil.validateRequestMap(map, "id");
            Map<String, Object> sourceMap = this.getProcessGroup(MapUtils.getString(map, "id"));
            // ????????????
            NifiProcessUtil.checkPermissions(sourceMap);

            //??????????????????
            Map<String, Object> req = NifiProcessUtil.postParam(map, (Map<String, Object>) MapUtils.getMap(sourceMap, "revision"));
            String url = NifiProcessUtil.assemblyUrl(biProperties.getNifiUrl(), NifiEnum.PROCSS_GROUPS.getKey(), MapUtils.getString(map, "id"));
            logger.info("NifiProcessServiceImpl.updProcessGroup, URL:{} ,REQUEST:{}", url, JsonUtil.obj2String(req));
            String response = HttpClientUtil.put(url, super.setHeaderAuthorization(), req);
            return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            logger.error("NifiProcessServiceImpl.updProcessGroup.error:", e);
            throw new BizException(ResourceMessageEnum.SYSTEM_ERROR.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.SYSTEM_ERROR.getMessage(), ThreadLocalHolder.getLang()));
        }
    }

    @Override
    public Map<String, Object> runState(String id, String state, boolean isGroup) {
        logger.info("NifiProcessServiceImpl.runState, id:{} ,state:{}", id, state);
        try {
            if (StringUtil.isEmpty(id) || StringUtil.isEmpty(state)) {
                throw new NifiException("NifiProcessServiceImpl.runState.error : ??????????????????");
            }
            Map<String, Object> prcessorMap = null;
            String url;
            if (isGroup) {
                prcessorMap = this.getProcessGroup(id);
                url = NifiProcessUtil.assemblyUrl(biProperties.getNifiUrl(), NifiEnum.RUN_PROCESSGROUP.getKey(), id);
            } else {
                prcessorMap = this.getProcessor(id);
                url = NifiProcessUtil.assemblyUrl(biProperties.getNifiUrl(), NifiEnum.RUN_PROCESSOR.getKey(), id);
            }

            // ????????????
            NifiProcessUtil.checkPermissions(prcessorMap);

            //??????????????????
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
        } catch (Exception e) {
            logger.error("NifiProcessServiceImpl.runState.error:", e);
            throw new BizException(ResourceMessageEnum.SYSTEM_ERROR.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.SYSTEM_ERROR.getMessage(), ThreadLocalHolder.getLang()));
        }
    }

    @Override
    public Map<String, Object> clearRequest(String processorsId) {
        try {
            if (StringUtil.isEmpty(processorsId)) {
                throw new NifiException("NifiProcessServiceImpl.clearRequest.error : ??????????????????");
            }
            String url = NifiProcessUtil.assemblyUrl(biProperties.getNifiUrl(), NifiEnum.CLEAR_REQUEST.getKey(), processorsId);
            logger.info("NifiProcessServiceImpl.clearRequest, URL:{} ,REQUEST:{}", url, null);
            String response = HttpClientUtil.post(url, super.setHeaderAuthorization(), null);
            return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            logger.error("NifiProcessServiceImpl.clearRequest.error:", e);
            throw new BizException(ResourceMessageEnum.SYSTEM_ERROR.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.SYSTEM_ERROR.getMessage(), ThreadLocalHolder.getLang()));
        }
    }

    @Override
    public Map<String, Object> terminate(String processorId) {
        try {
            if (StringUtil.isEmpty(processorId)) {
                throw new NifiException("NifiProcessServiceImpl.terminate.error : ??????????????????");
            }
            String url = NifiProcessUtil.assemblyUrl(biProperties.getNifiUrl(), NifiEnum.TERMINATE.getKey(), processorId);
            //??????????????????
            logger.info("NifiProcessServiceImpl.terminate, URL:{} ", url);
            String response = HttpClientUtil.delete(url, super.setHeaderAuthorization());
            return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            logger.error("NifiProcessServiceImpl.terminate.error:", e);
            throw new BizException(ResourceMessageEnum.SYSTEM_ERROR.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.SYSTEM_ERROR.getMessage(), ThreadLocalHolder.getLang()));
        }
    }

    @Override
    public Map<String, Object> getMax(String processorsId) throws Exception {
        if (StringUtil.isEmpty(processorsId)) {
            throw new NifiException("NifiProcessServiceImpl.getMax.error : ??????????????????");
        }
        String url = NifiProcessUtil.assemblyUrl(biProperties.getNifiUrl(), NifiEnum.GET_MAX.getKey(), processorsId);
        logger.info("NifiProcessServiceImpl.getMax, URL:{} ", url);
        String response = HttpClientUtil.get(url, super.setHeaderAuthorization(), null);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> createControllerService(Map<String, Object> map) {
        try {
            NifiProcessUtil.validateRequestMap(map, "type", "name", "dbUser", "passWord", "dbUrl", "driverName", "driverLocations");

            String id = MapUtils.getString(map, "id");
            //?????????????????????scope???rootGroup???Id
            if (StringUtil.isEmpty(id)) {
                Map<String, Object> rootGroupInfos = this.getRootGroupInfo();
                //????????????
                NifiProcessUtil.checkPermissions(rootGroupInfos);
                Map processGroupFlowMap = MapUtils.getMap(rootGroupInfos, "processGroupFlow");
                id = MapUtils.getString(processGroupFlowMap, "id");
            }

            Map<String, Object> param = Maps.newHashMap();
            //???????????????
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
            String url = NifiProcessUtil.assemblyUrl(biProperties.getNifiUrl(), NifiEnum.CREATE_CONTROLLER_SERVICE.getKey(), id);
            logger.info("NifiProcessServiceImpl.createControllerService, URL:{} ,REQUEST:{}", url, JsonUtil.obj2String(req));
            String response = HttpClientUtil.post(url, super.setHeaderAuthorization(), req);
            return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            logger.error("NifiProcessServiceImpl.createControllerService.error:", e);
            throw new BizException(ResourceMessageEnum.SYSTEM_ERROR.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.SYSTEM_ERROR.getMessage(), ThreadLocalHolder.getLang()));
        }
    }

    @Override
    public Map<String, Object> createOtherControllerService(Map<String, Object> map) {
        try {
            NifiProcessUtil.validateRequestMap(map, "type", "name");

            String id = MapUtils.getString(map, "id");
            // ?????????????????????scope???rootGroup???Id
            if (StringUtil.isEmpty(id)) {
                Map<String, Object> rootGroupInfos = this.getRootGroupInfo();
                //????????????
                NifiProcessUtil.checkPermissions(rootGroupInfos);
                Map processGroupFlowMap = MapUtils.getMap(rootGroupInfos, "processGroupFlow");
                id = MapUtils.getString(processGroupFlowMap, "id");
            }

            Map<String, Object> param = Maps.newHashMap();
            // ???????????????
            param.put("type", MapUtils.getString(map, "type"));
            param.put("name", MapUtils.getString(map, "name"));
            param.put("comments", MapUtils.getString(map, "comments"));

            Map<String, Object> properties = Maps.newHashMap();
            properties.putAll(map);
            properties.remove("type");
            properties.remove("name");
            properties.remove("comments");
            properties.remove("id");
            param.put("properties", properties);

            Map<String, Object> req = NifiProcessUtil.postParam(param);
            String url = NifiProcessUtil.assemblyUrl(biProperties.getNifiUrl(), NifiEnum.CREATE_CONTROLLER_SERVICE.getKey(), id);
            logger.info("NifiProcessServiceImpl.createControllerService, URL:{} ,REQUEST:{}", url, JsonUtil.obj2String(req));
            String response = HttpClientUtil.post(url, super.setHeaderAuthorization(), req);
            return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            logger.error("NifiProcessServiceImpl.createOtherControllerService.error:", e);
            throw new BizException(ResourceMessageEnum.SYSTEM_ERROR.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.SYSTEM_ERROR.getMessage(), ThreadLocalHolder.getLang()));
        }
    }

    @Override
    public Map<String, Object> runControllerService(String id, String state) {
        try {
            if (StringUtil.isEmpty(id) || StringUtil.isEmpty(state)) {
                throw new NifiException("NifiProcessServiceImpl.runControllerService.error ??????:??????????????????");
            }
            Map<String, Object> prcessorMap = this.getControllerService(id);

            // ????????????
            NifiProcessUtil.checkPermissions(prcessorMap);
            //??????????????????
            Map<String, Object> req = NifiProcessUtil.postParam(null, (Map<String, Object>) MapUtils.getMap(prcessorMap, "revision"));
            String controllerServiceState = EffectEnum.ENABLE.getKey().equals(state) ? "ENABLED" : "DISABLED";
            req.put("state", controllerServiceState);
            String url = NifiProcessUtil.assemblyUrl(biProperties.getNifiUrl(), NifiEnum.RUN_CONTROLLER_SERVICE.getKey(), id);
            logger.info("NifiProcessServiceImpl.runControllerService, URL:{} ,REQUEST:{}", url, JsonUtil.obj2String(req));
            String response = HttpClientUtil.put(url, super.setHeaderAuthorization(), req);
            return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            logger.error("NifiProcessServiceImpl.runControllerService.error:", e);
            throw new BizException(ResourceMessageEnum.SYSTEM_ERROR.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.SYSTEM_ERROR.getMessage(), ThreadLocalHolder.getLang()));
        }
    }

    @Override
    public Map<String, Object> getControllerService(String id) throws Exception {
        if (StringUtil.isEmpty(id)) {
            throw new NifiException("NifiProcessServiceImpl.getControllerService.error : id????????????");
        }

        String url = NifiProcessUtil.assemblyUrl(biProperties.getNifiUrl(), NifiEnum.CONTROLLER_SERVICE.getKey(), id);
        logger.info("NifiProcessServiceImpl.getControllerService ??????, URL:{}", url);
        String response = HttpClientUtil.get(url, super.setHeaderAuthorization(), null);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> delControllerService(String id) {
        try {
            if (StringUtil.isEmpty(id)) {
                throw new NifiException("NifiProcessServiceImpl.delControllerService.error : id????????????");
            }
            Map<String, Object> connectMap = this.getControllerService(id);
            // ????????????
            NifiProcessUtil.checkPermissions(connectMap);
            String url = NifiProcessUtil.assemblyUrl(biProperties.getNifiUrl(), NifiEnum.CONTROLLER_SERVICE.getKey(), id);

            Map<String, Object> headers = super.setHeaderAuthorization();
            url = url + "?version=" + MapUtils.getMap(connectMap, "revision").get("version");
            logger.info("NifiProcessServiceImpl.delControllerService ??????, URL:{} ", url);

            String response = HttpClientUtil.delete(url, headers);
            return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            logger.error("NifiProcessServiceImpl.delControllerService.error:", e);
            throw new BizException(ResourceMessageEnum.SYSTEM_ERROR.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.SYSTEM_ERROR.getMessage(), ThreadLocalHolder.getLang()));
        }
    }

    @Override
    public Map<String, Object> updControllerService(Map<String, Object> map) {
        try {
            NifiProcessUtil.validateRequestMap(map, "id");
            Map<String, Object> prcessorMap = this.getControllerService(MapUtils.getString(map, "id"));
            // ????????????
            NifiProcessUtil.checkPermissions(prcessorMap);

            //??????????????????
            Map<String, Object> req = NifiProcessUtil.postParam(map, (Map<String, Object>) MapUtils.getMap(prcessorMap, "revision"));
            String url = NifiProcessUtil.assemblyUrl(biProperties.getNifiUrl(), NifiEnum.CONTROLLER_SERVICE.getKey(), MapUtils.getString(map, "id"));
            logger.info("NifiProcessServiceImpl.updControllerService, URL:{} ,REQUEST:{}", url, JsonUtil.obj2String(req));
            String response = HttpClientUtil.put(url, super.setHeaderAuthorization(), req);
            return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            logger.error("NifiProcessServiceImpl.updControllerService.error:", e);
            throw new BizException(ResourceMessageEnum.SYSTEM_ERROR.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.SYSTEM_ERROR.getMessage(), ThreadLocalHolder.getLang()));
        }
    }

    @Override
    public Map<String, Object> createProcessor(Map<String, Object> map) throws Exception {
        NifiProcessUtil.validateRequestMap(map, "id", "type");
        String id = MapUtils.getString(map, "id");
        // ????????????
        NifiProcessUtil.checkPermissions(this.getProcessGroup(id));
        //??????id
        map.remove("id");
        Map<String, Object> req = NifiProcessUtil.postParam(map);
        String url = NifiProcessUtil.assemblyUrl(biProperties.getNifiUrl(), NifiEnum.CREATE_PROCESSOR.getKey(), id);
        logger.info("NifiProcessServiceImpl.createProcessor, URL:{} ,REQUEST:{}", url, JsonUtil.obj2String(req));
        String response = HttpClientUtil.post(url, super.setHeaderAuthorization(), req);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> getProcessor(String id) throws Exception {
        if (StringUtil.isEmpty(id)) {
            throw new BizException(ResourceMessageEnum.ID_NULL_ERROR.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.ID_NULL_ERROR.getMessage(), ThreadLocalHolder.getLang()));
        }

        String url = NifiProcessUtil.assemblyUrl(biProperties.getNifiUrl(), NifiEnum.PROCESSORS.getKey(), id);
        logger.info("NifiProcessServiceImpl.getProcessor ??????, URL:{} ", url);
        String response = HttpClientUtil.get(url, super.setHeaderAuthorization(), null);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> updateProcessor(String processorId, Map<String, Object> map) throws Exception {
        if (StringUtil.isEmpty(processorId)) {
            throw new NifiException("updateProcessor:processorId????????????");
        }
        Map<String, Object> prcessorMap = this.getProcessor(processorId);
        // ????????????
        NifiProcessUtil.checkPermissions(prcessorMap);

        //??????????????????
        // {
        ////        "revision":{
        ////        "version":"0"
        ////        },
        ////        "id":"9fdef751-51d6-38ba-bf17-7b1bb0ebf55f",
        ////        "component":{
        ////        },
        ////        "disconnectedNodeAcknowledged":false
        ////        }
        Map<String, Object> req = Maps.newHashMap();
        req.put("disconnectedNodeAcknowledged", false);
        req.put("component", map);
        req.put("id", processorId);
        Map<String, Object> revision = (Map<String, Object>) MapUtils.getMap(prcessorMap, "revision");
        if (null != revision) {
            revision.remove("clientId");
            req.put("revision", revision);
        }

        String url = NifiProcessUtil.assemblyUrl(biProperties.getNifiUrl(), NifiEnum.PROCESSORS.getKey(), processorId);
        logger.info("NifiProcessServiceImpl.updateProcessor, URL:{} ,REQUEST:{}", url, JsonUtil.obj2String(req));
        String response = HttpClientUtil.put(url, super.setHeaderAuthorization(), req);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> delProcessor(String id) throws Exception {
        if (StringUtil.isEmpty(id)) {
            throw new NifiException("NifiProcessServiceImpl.delProcessor:id????????????");
        }
        Map<String, Object> processor = this.getProcessor(id);
        // ????????????
        NifiProcessUtil.checkPermissions(processor);
        String url = NifiProcessUtil.assemblyUrl(biProperties.getNifiUrl(), NifiEnum.PROCESSORS.getKey(), id);

        Map<String, Object> headers = super.setHeaderAuthorization();
        url = url + "?version=" + MapUtils.getMap(processor, "revision").get("version");
        logger.info("NifiProcessServiceImpl.delProcessor ??????, URL:{} ", url);

        String response = HttpClientUtil.delete(url, headers);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> createConnections(Map<String, Object> map, String id) throws Exception {
        if (StringUtil.isEmpty(id)) {
            throw new NifiException("createConnections error: id????????????");
        }
        Map<String, Object> prcessorGroupMap = this.getProcessGroup(id);
        // ????????????
        NifiProcessUtil.checkPermissions(prcessorGroupMap);
        //??????????????????
        Map<String, Object> req = NifiProcessUtil.postParam(map);
        String url = NifiProcessUtil.assemblyUrl(biProperties.getNifiUrl(), NifiEnum.CREATE_CONNECTIONS.getKey(), id);
        logger.info("NifiProcessServiceImpl.createConnections, URL:{} ,REQUEST:{}", url, JsonUtil.obj2String(req));
        String response = HttpClientUtil.post(url, super.setHeaderAuthorization(), req);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> getConnections(String id) throws Exception {
        if (StringUtil.isEmpty(id)) {
            throw new NifiException("getConnections??????:id????????????");
        }

        String url = NifiProcessUtil.assemblyUrl(biProperties.getNifiUrl(), NifiEnum.CONNECTIONS.getKey(), id);
        logger.info("NifiProcessServiceImpl.getConnections ??????, URL:{} ", url);
        String response = HttpClientUtil.get(url, super.setHeaderAuthorization(), null);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> dropConnections(String id) {
        try {
            if (StringUtil.isEmpty(id)) {
                throw new NifiException("dropConnections??????:id????????????");
            }

            String url = NifiProcessUtil.assemblyUrl(biProperties.getNifiUrl(), NifiEnum.DROP_CONNECTIONS.getKey(), id);
            logger.info("NifiProcessServiceImpl.dropConnections ??????, URL:{} ", url);
            String response = HttpClientUtil.post(url, super.setHeaderAuthorization(), null);
            return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            logger.error("NifiProcessServiceImpl.dropConnections.error:", e);
            throw new BizException(ResourceMessageEnum.SYSTEM_ERROR.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.SYSTEM_ERROR.getMessage(), ThreadLocalHolder.getLang()));
        }
    }

    @Override
    public Map<String, Object> delConnections(String id) throws Exception {
        if (StringUtil.isEmpty(id)) {
            throw new NifiException("NifiProcessServiceImpl.delConnections.error:id????????????");
        }
        Map<String, Object> connectMap = this.getConnections(id);
        // ????????????
        NifiProcessUtil.checkPermissions(connectMap);
        String url = NifiProcessUtil.assemblyUrl(biProperties.getNifiUrl(), NifiEnum.CONNECTIONS.getKey(), id);

        Map<String, Object> headers = super.setHeaderAuthorization();
        url = url + "?version=" + MapUtils.getMap(connectMap, "revision").get("version");
        logger.info("NifiProcessServiceImpl.delConnections ??????, URL:{} ", url);

        String response = HttpClientUtil.delete(url, headers);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> getListingRequest(String connectionId) throws Exception {
        if (StringUtil.isEmpty(connectionId)) {
            throw new NifiException("NifiProcessServiceImpl.getListing.error :connectionId ????????????");
        }

        String url = NifiProcessUtil.assemblyUrl(biProperties.getNifiUrl(), NifiEnum.LISTING_REQUESTS.getKey(), connectionId);
        logger.info("NifiProcessServiceImpl.getListing ??????, URL:{} ", url);
        String response = HttpClientUtil.post(url, super.setHeaderAuthorization(), null);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Map<String, Object> getFlowFileList(String connectionId, String requestId) throws Exception {
        if (StringUtil.isEmpty(connectionId) || StringUtil.isEmpty(requestId)) {
            throw new NifiException("NifiProcessServiceImpl.getFlowFileList.error : id ????????????");
        }

        String url = NifiProcessUtil.assemblyUrl(biProperties.getNifiUrl(), NifiEnum.LISTING_FLOWFILE_IDS.getKey(), connectionId, requestId);
        logger.info("NifiProcessServiceImpl.getFlowFileList ??????, URL:{} ", url);
        String response = HttpClientUtil.get(url, super.setHeaderAuthorization(), null);
        return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public String getFlowFileContent(String connectionId, String flowFileId, String clusterNodeId) throws Exception {
        if (StringUtil.isEmpty(connectionId) || StringUtil.isEmpty(flowFileId) || StringUtil.isEmpty(clusterNodeId)) {
            throw new NifiException("NifiProcessServiceImpl.getFlowFileContent.error : id ????????????");
        }

        String url = NifiProcessUtil.assemblyUrl(biProperties.getNifiUrl(), NifiEnum.LISTING_FLOWFILE_CONTENT.getKey(), connectionId, flowFileId);
        url = url + "?clusterNodeId=" + clusterNodeId;
        logger.info("NifiProcessServiceImpl.getFlowFileContent ??????, URL:{} ", url);
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
            throw new NifiException("NifiProcessServiceImpl.preview error : ??????????????????");
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
            throw new NifiException("NifiProcessServiceImpl.preview error : ??????????????????");
        }

        //todo ???????????????????????? ??????????????????????????????
        Integer size = MapUtils.getInteger(flowFileSummaries.get(0), "size");
        if (size == 0) {
            throw new NifiException("NifiProcessServiceImpl.preview error : ??????????????????");
        }
        String uuid = MapUtils.getString(flowFileSummaries.get(0), "uuid");
        String clusterNodeId = MapUtils.getString(flowFileSummaries.get(0), "clusterNodeId");
        return getFlowFileContent(connectionId, uuid, clusterNodeId);
    }

    @Override
    public Map<String, Object> createByTemplate(String processGroupId, String json) {
        try {
            if (StringUtil.isEmpty(processGroupId)) {
                throw new NifiException("NifiProcessServiceImpl.createByTemplate.error: ??????????????????");
            }
            String url = NifiProcessUtil.assemblyUrl(biProperties.getNifiUrl(), NifiEnum.CREATE_BY_TEMPLATE.getKey(), processGroupId);
            logger.info("NifiProcessServiceImpl.createConnections, URL:{} ,REQUEST:{}", url, json);
            String response = HttpClientUtil.postJson(url, super.setHeaderAuthorization(), json);
            return JsonUtil.string2Obj(response, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            logger.error("NifiProcessServiceImpl.createByTemplate.error:", e);
            throw new BizException(ResourceMessageEnum.SYSTEM_ERROR.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.SYSTEM_ERROR.getMessage(), ThreadLocalHolder.getLang()));

        }
    }

    @Override
    public String getTemplate() throws Exception {
        logger.info("NifiProcessServiceImpl.cluster, URL:{}", biProperties.getNifiUrl() + NifiEnum.TEMLATES.getKey());
        String response = HttpClientUtil.get(biProperties.getNifiUrl() + NifiEnum.TEMLATES.getKey(), super.setHeaderAuthorization(), null);
        Object jsonObject = JSONObject.parse(response);
        if (null == jsonObject) {
            logger.error("????????????????????????");
            throw new BizException(ResourceMessageEnum.SYSTEM_ERROR.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.SYSTEM_ERROR.getMessage(), ThreadLocalHolder.getLang()));
        }
        Object jsonArray = ((JSONObject) jsonObject).get("templates");
        if (null == jsonArray) {
            logger.error("??????????????????????????????");
            throw new BizException(ResourceMessageEnum.SYSTEM_ERROR.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.SYSTEM_ERROR.getMessage(), ThreadLocalHolder.getLang()));
        }
        for (Object object : (JSONArray) jsonArray) {
            JSONObject template = (JSONObject) ((JSONObject) object).get("template");
            if (template.getString("name").equalsIgnoreCase("bi_template_important")) {
                return template.getString("id");
            }
        }
        logger.error("????????????????????????");
        throw new BizException(ResourceMessageEnum.SYSTEM_ERROR.getCode(),
                localeMessageService.getMessage(ResourceMessageEnum.SYSTEM_ERROR.getMessage(), ThreadLocalHolder.getLang()));
    }


}
