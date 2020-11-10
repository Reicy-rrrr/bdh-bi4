package com.deloitte.bdh.data.collation.nifi.template;



public enum TemplateEnum {

    SYNC_SQL("SYNC_SQL", "同步数据源的组件"),

    OUT_SQL("OUT_SQL", "ETL到数据库"),


    ;


    private String key;
    private String value;

    TemplateEnum(String key, String value) {
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
