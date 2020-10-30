package com.deloitte.bdh.data.collation.enums;


public enum PlanStageEnum {

    TO_EXECUTE("TO_EXECUTE", "待执行"),
    EXECUTING("EXECUTING", "执行中"),
    EXECUTED("EXECUTED", "执行完成"),

    ;

    private String key;

    private String value;

    PlanStageEnum(String key, String value) {
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
