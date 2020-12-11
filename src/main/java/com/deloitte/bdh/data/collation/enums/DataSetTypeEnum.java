package com.deloitte.bdh.data.collation.enums;


import org.apache.commons.lang3.StringUtils;

public enum DataSetTypeEnum {

    DIRECT("0", "直连", ".D"),
    MODEL("1", "整理", ".M"),

    ;

    private String key;
    private String value;
    private String suffix;

    DataSetTypeEnum(String key, String value, String suffix) {
        this.key = key;
        this.value = value;
        this.suffix = suffix;

    }

    public static DataSetTypeEnum getEnumByKey(Object key) {
        DataSetTypeEnum[] enums = DataSetTypeEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (StringUtils.equals(key.toString(), enums[i].getKey().toString())) {
                return enums[i];
            }
        }
        throw new RuntimeException("未找到对应的类型");
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getSuffix() {
        return suffix;
    }
}
