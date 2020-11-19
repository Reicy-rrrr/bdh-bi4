package com.deloitte.bdh.data.collation.enums;


import org.apache.commons.lang3.StringUtils;

public enum SyncTypeEnum {

    DIRECT(0, "直连"),
    FULL(1, "全量同步"),
    INCREMENT(2, "增量同步"),
    LOCAL(3, "本地"),

    ;

    private Integer key;

    private String value;

    SyncTypeEnum(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public static SyncTypeEnum getEnumByKey(Object key) {
        SyncTypeEnum[] enums = SyncTypeEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (StringUtils.equals(key.toString(), enums[i].getKey().toString())) {
                return enums[i];
            }
        }
        throw new RuntimeException("未找到对应的类型");
    }

    public Integer getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
