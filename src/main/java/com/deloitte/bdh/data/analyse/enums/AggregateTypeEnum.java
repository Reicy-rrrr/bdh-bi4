package com.deloitte.bdh.data.analyse.enums;


public enum AggregateTypeEnum {
    SUM("SUM", "求和"),
    AVG("AVG", "均值"),

    ;

    private final String key;

    private final String vlaue;


    AggregateTypeEnum(String key, String vlaue) {
        this.key = key;
        this.vlaue = vlaue;
    }


    public String getKey() {
        return key;
    }

    public String getVlaue() {
        return vlaue;
    }

}
