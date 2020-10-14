package com.deloitte.bdh.data.enums;

import com.deloitte.bdh.common.util.NifiProcessUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * @author liuwei
 * @date 2020/09/22
 */
public enum NifiEnum {

    /**
     * key of token
     */
    REDIS_ACCESS_TOKEN("REDIS:ACCESS:TOKEN", "登陆验证码"),

    /**
     * 从NIFI获取token
     */
    ACCESS_TOKEN("/nifi-api/access/token", "从NIFI获取token"),

    /**
     * 获取集群状态
     */
    NIFI_CLUSTER("/nifi-api/controller/cluster", "获取集群状态"),

    /**
     * 获取RootGroup Id
     */
    ROOT_GROUP_INFO("/nifi-api/flow/process-groups/root", "获取RootGroup Id"),

    /**
     * 创建 ProcessGroup
     */
    CREATE_PROCSS_GROUP("/nifi-api/process-groups/" + NifiProcessUtil.TEMP + "/process-groups", "创建 ProcessGroup"),

    /**
     * 查看、修改 、删除ProcessGroup
     */
    PROCSS_GROUPS("/nifi-api/process-groups/" + NifiProcessUtil.TEMP, "查看、修改 ProcessGroup"),

    /**
     * 创建 ControllerService
     */
    CREATE_CONTROLLER_SERVICE("/nifi-api/process-groups/" + NifiProcessUtil.TEMP + "/controller-services", "创建 ControllerService"),

    /**
     * 运行 ControllerService
     */
    RUN_CONTROLLER_SERVICE("/nifi-api/controller-services/" + NifiProcessUtil.TEMP + "/run-status", "运行 ControllerService"),

    /**
     * 查询 ControllerService
     */
    CONTROLLER_SERVICE("/nifi-api/controller-services/" + NifiProcessUtil.TEMP, "查询 ControllerService"),

    /**
     * 创建 processor
     */
    CREATE_PROCESSOR("/nifi-api/process-groups/" + NifiProcessUtil.TEMP + "/processors", "创建 processor"),

    /**
     * 查询/修改/删除 PROCESSOR
     */
    PROCESSORS("/nifi-api/processors/" + NifiProcessUtil.TEMP, "查询 PROCESSOR"),

    /**
     * 创建 connections
     */
    CREATE_CONNECTIONS("/nifi-api/process-groups/" + NifiProcessUtil.TEMP + "/connections", "创建 connections"),

    /**
     * 清空 connections
     */
    DROP_CONNECTIONS("/nifi-api/flowfile-queues/" + NifiProcessUtil.TEMP + "/drop-requests", "清空 connections"),

    /**
     * 查看/删除 connections
     */
    CONNECTIONS("/nifi-api/connections/" + NifiProcessUtil.TEMP, "查看/删除 connections"),

    /**
     * run-processor
     */
    RUN_PROCESSOR("/nifi-api/processors/" + NifiProcessUtil.TEMP + "/run-status", "run-status"),

    /**
     * run-processGroup
     */
    RUN_PROCESSGROUP("/nifi-api/flow/process-groups/" + NifiProcessUtil.TEMP, "run-processGroup"),

    /**
     * 查看listing-requests
     */
    LISTING_REQUESTS("/nifi-api/flowfile-queues/" + NifiProcessUtil.TEMP + "/listing-requests", "查看listing-requests"),

    /**
     * 查看FlowFiles ids
     */
    LISTING_FLOWFILE_IDS("/nifi-api/flowfile-queues/" + NifiProcessUtil.TEMP + "/listing-requests/" + NifiProcessUtil.TEMP, "查看FlowFiles ids"),

    /**
     * 查看FlowFiles content
     */
    LISTING_FLOWFILE_CONTENT("/nifi-api/flowfile-queues/" + NifiProcessUtil.TEMP + "/flowfiles/" + NifiProcessUtil.TEMP + "/content", "查看FlowFiles content"),

    ;

    private String key;

    private String desc;

    NifiEnum(String key, String desc) {
        this.key = key;
        this.desc = desc;
    }

    /**
     * 根据缓存key获取描述
     *
     * @param key 环境key
     * @return String
     */
    public static String getKeyDesc(String key) {
        NifiEnum[] enums = NifiEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (StringUtils.equals(key, enums[i].getKey())) {
                return enums[i].getDesc();
            }
        }
        return "";
    }

    public String getKey() {
        return key;
    }

    public String getDesc() {
        return desc;
    }
}
