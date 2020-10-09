package com.deloitte.bdh.data.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * nifi controller service 类型
 */
public enum ServiceTypeEnum {

    CSVReader("org.apache.nifi.csv.CSVReader", "CSV文件阅读器"),
    JsonRecordSetWriter("org.apache.nifi.json.JsonRecordSetWriter", "JSON记录集编写器"),
    AvroSchemaRegistry("org.apache.nifi.schemaregistry.services.AvroSchemaRegistry", "JSON格式方案"),

    ;

    private String key;

    private String value;

    ServiceTypeEnum(String key, String value) {
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
        ServiceTypeEnum[] enums = ServiceTypeEnum.values();
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
