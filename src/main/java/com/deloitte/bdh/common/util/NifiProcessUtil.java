package com.deloitte.bdh.common.util;

import com.deloitte.bdh.data.enums.SourceTypeEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
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
     * 组装 请求参数(用于变更)
     */
    public static Map<String, Object> postParam(Map<String, Object> req, Map<String, Object> revision) {
        //统一设置请求参数
        Map<String, Object> postParam = Maps.newHashMap();

        // revision 里的 version 必须一样,移除 clientId
        revision.remove("clientId");
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
                throw new RuntimeException(String.format("参数校验失败,参数%s不能为空", arge));
            }
        }
    }


    /**
     * 校验processor
     */
    public static Boolean validateProcessor(Map<String, Object> map) {
        if (MapUtils.isEmpty(map)) {
            throw new RuntimeException("参数校验失败:参数异常");
        }

        //validationStatus：VALID&INVALID ,state:STOPPED
        if (MapUtils.getString(map, "validationStatus").equalsIgnoreCase("INVALID")) {
            return false;
        }
        return true;
    }

    /**
     * 获取processor relationship列表（创建connections时使用 ）
     */
    public static List<String> getRela(Map<String, Object> map) {
        if (MapUtils.isEmpty(map)) {
            throw new RuntimeException("获取relationship失败:参数异常");
        }
        Object rela = MapUtils.getObject(map, "relationships");
        List<String> relas = Lists.newArrayList();
        if (null != rela) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) rela;
            for (Map<String, Object> args0 : list) {
                relas.add(MapUtils.getString(args0, "autoTerminate"));
            }
        }
        return relas;
    }

    /**
     * 设置 processor relationship状态(一般最后一个节点用)
     */
    public void setRelaAutoTerminateTrue(Map<String, Object> map) {
        if (MapUtils.isEmpty(map)) {
            throw new RuntimeException("获取relationship失败:参数异常");
        }
        Object rela = MapUtils.getObject(map, "relationships");
        if (null == rela) {
            throw new RuntimeException(String.format("设置relationship 失败，未找到相关数据:%s", JsonUtil.obj2String(map)));
        }
        List<Map<String, Object>> list = (List<Map<String, Object>>) rela;
        for (Map<String, Object> args0 : list) {
            args0.put("autoTerminate", true);
        }
        map.put("relationships", list);
    }


    /**
     * 组装DbURL
     */
    public static String getDbUrl(String type, String ip, String port, String dbName) {
        String url = SourceTypeEnum.getUrlByType(type);
        return url.replace("IP", ip).replace("PORT", port).replace("DBNAME", dbName);
    }


    /**
     * 获取version 字符串
     */
    public static String getVersion(Map<String, Object> sourceMap) {
        Map map = MapUtils.getMap(sourceMap, "revision");
        if (MapUtils.isEmpty(map)) {
            return null;
        }
        return MapUtils.getString(map, "version");
    }

    public static void main(String[] args) {
    }
}
