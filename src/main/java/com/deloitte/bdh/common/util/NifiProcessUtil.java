package com.deloitte.bdh.common.util;

import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;

import java.util.Map;

public class NifiProcessUtil {
    public static final String TEMP = "TEMP";

    private NifiProcessUtil() {
    }

    /**
     * 组装nifi特殊 url
     */
    public static String assemblyUrl(String url, String source) {
        return assemblyUrl(url, source, null);
    }

    /**
     * 组装nifi特殊 url
     */
    public static String assemblyUrl(String url, String source, String id) {
        StringBuilder stringBuilder = new StringBuilder(url);
        if (!StringUtil.isEmpty(id)) {
            source = source.replace(TEMP, id);
        }
        stringBuilder.append(source);
        return stringBuilder.toString();
    }

    /**
     * 验证权限（默认读写权限）
     */
    public static void checkPermissions(Map<String, Object> map) {
        checkPermissions(map, 1);
    }

    /**
     * 验证权限
     */
    public static void checkPermissions(Map<String, Object> map, Integer type) {
        if (MapUtils.isEmpty(map)) {
            throw new RuntimeException("验证权限失败:参数不能为空");
        }
        Map permissions = MapUtils.getMap(map, "permissions");
        if (MapUtils.isEmpty(permissions)) {
            throw new RuntimeException("验证权限失败:没有permissions相关数据");
        }
        Boolean canRead = MapUtils.getBoolean(permissions, "canRead");
        Boolean canWrite = MapUtils.getBoolean(permissions, "canWrite");

        if (0 == type) {
            if (!canRead) {
                throw new RuntimeException("验证权限失败:没有读权限");
            }
        } else {
            if (!canWrite) {
                throw new RuntimeException("验证权限失败:没有写权限");
            }
        }
    }

    /**
     * 组装 position 请求参数
     */
    public static Map<String, Object> position(Map<String, Object> req, Map<String, Object> posiMap) {
        if (MapUtils.isEmpty(posiMap)) {
            return req;
        }
        Map<String, Object> position = Maps.newHashMap();
        position.put("x", MapUtils.getString(posiMap, "x"));
        position.put("y", MapUtils.getString(posiMap, "y"));
        req.put("position", position);
        return req;
    }

    /**
     * 组装 post 请求参数(用于新增)
     */
    public static Map<String, Object> postParam(Map<String, Object> req) {
        //设置version
        Map<String, Object> revision = Maps.newHashMap();
        revision.put("clientId", "");
        revision.put("version", 0);
        return postParam(req, revision);
    }

    /**
     * 组装 post 请求参数(用于变更)
     */
    public static Map<String, Object> postParam(Map<String, Object> req, Map<String, Object> revision) {
        //todo revision 待处理
        //统一设置请求参数
        Map<String, Object> postParam = Maps.newHashMap();
        postParam.put("revision", revision);
        postParam.put("disconnectedNodeAcknowledged", false);
        postParam.put("component", req);
        return postParam;
    }

    /**
     * 校验请求参数
     */
    public static void validateRequestMap(Map<String, Object> map, String... args) {
        if (null == args || args.length == 0) {
            return;
        }
        if (MapUtils.isEmpty(map)) {
            throw new RuntimeException("参数校验失败:参数异常");
        }
        for (String arge : args) {
            if (StringUtil.isEmpty(MapUtils.getString(map, arge))) {
                throw new RuntimeException(String.format("参数校验失败,参数{}不能为空", arge));
            }
        }
    }
}
