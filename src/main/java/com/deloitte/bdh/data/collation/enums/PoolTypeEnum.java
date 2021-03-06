package com.deloitte.bdh.data.collation.enums;

import org.apache.commons.lang3.StringUtils;

public enum PoolTypeEnum {

    DBCPConnectionPool("org.apache.nifi.dbcp.DBCPConnectionPool", "DBCPConnectionPool连接池"),
    HiveConnectionPool("org.apache.nifi.dbcp.hive.HiveConnectionPool", "HiveConnectionPool连接池"),
    AvroSchemaRegistry("org.apache.nifi.schemaregistry.services.AvroSchemaRegistry", "AvroSchemaRegistry"),
    CSVReader("org.apache.nifi.csv.CSVReader", "CSVReader"),
    JsonRecordSetWriter("org.apache.nifi.json.JsonRecordSetWriter", "JsonRecordSetWriter"),
    ;

    private String key;

    private String value;

    PoolTypeEnum(String key, String value) {
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
        PoolTypeEnum[] enums = PoolTypeEnum.values();
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
