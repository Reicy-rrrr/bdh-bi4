package com.deloitte.bdh.data.collation.enums;

import org.apache.commons.lang3.StringUtils;

public enum DownLoadTStatusEnum {

    SUCCESS("1", "成功"),

    FAIL("0", "失败"),

    ING("2", "处理中");

    private String key;

    private String value;

    DownLoadTStatusEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * 根据缓存key获取描述
     *
     * @param key 环境key
     * @return String
     */
    public static DownLoadTStatusEnum getEnum(String key) {
        DownLoadTStatusEnum[] enums = DownLoadTStatusEnum.values();
        for (DownLoadTStatusEnum anEnum : enums) {
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