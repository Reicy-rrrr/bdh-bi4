package com.deloitte.bdh.data.collation.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @author liuwei
 * @date 2020/09/22
 */
public enum EffectEnum {

    ENABLE("ENABLE", "有效"),
    DISABLE("DISABLE", "失效");

    private String key;

    private String value;

    EffectEnum(String key, String value) {
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
        EffectEnum[] enums = EffectEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (StringUtils.equals(key, enums[i].getKey())) {
                return enums[i].getvalue();
            }
        }
        return "";
    }

    public String getKey() {
        return key;
    }

    public String getvalue() {
        return value;
    }
}