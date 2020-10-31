package com.deloitte.bdh.data.collation.enums;


public enum PlanResultEnum {

    SUCCESS("1", "成功"),
    FAIL("0", "失败"),
    CANCEL("2", "取消"),

    ;

    private String key;

    private String value;

    PlanResultEnum(String key, String value) {
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
