package com.deloitte.bdh.data.collation.enums;

import org.apache.commons.lang3.StringUtils;

public enum YesOrNoEnum {

    YES("1", "是"),

    NO("0", "否");

    private String key;

    private String value;

    YesOrNoEnum(String key, String value) {
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
        YesOrNoEnum[] enums = YesOrNoEnum.values();
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