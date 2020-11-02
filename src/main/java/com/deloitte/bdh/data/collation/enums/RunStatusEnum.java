package com.deloitte.bdh.data.collation.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @author liuwei
 * @date 2020/09/22
 */
public enum RunStatusEnum {

    RUNNING("RUNNING", "运行中"),
    STOP("STOPPED", "已停止");

    private String key;

    private String value;

    RunStatusEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * 根据缓存key获取描述
     *
     * @param key 环境key
     * @return String
     */
    public static String getValue(String key) {
        RunStatusEnum[] enums = RunStatusEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (StringUtils.equals(key, enums[i].getKey())) {
                return enums[i].getvalue();
            }
        }
        return "";
    }

    public static RunStatusEnum getEnum(String key) {
        RunStatusEnum[] enums = RunStatusEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (StringUtils.equals(key, enums[i].getKey())) {
                return enums[i];
            }
        }
        throw new RuntimeException("未找到目标对象");
    }

    public String getKey() {
        return key;
    }

    public String getvalue() {
        return value;
    }
}