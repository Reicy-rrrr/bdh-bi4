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
    public static YesOrNoEnum getEnum(String key) {
        YesOrNoEnum[] enums = YesOrNoEnum.values();
        for (YesOrNoEnum anEnum : enums) {
            if (StringUtils.equals(key, anEnum.getKey())) {
                return anEnum;
            }
        }
        throw new RuntimeException("未找到对应的类型");
    }

    public String getKey() {
        return key;
    }

    public String getvalue() {
        return value;
    }
}