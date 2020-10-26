package com.deloitte.bdh.data.collation.enums;


public enum ComponentTypeEnum {

    DATASOURCE("DATASOURCE", "数据源"),
    JOIN("JOIN", "关联"),
    GROUP("GROUP", "聚合"),
    ARRANGE("ARRANGE", "整理"),
    OUT("OUT", "输出"),

    ;

    private String key;

    private String value;

    ComponentTypeEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }


    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
