package com.deloitte.bdh.data.collation.enums;


import org.apache.commons.lang3.StringUtils;

public enum PlanStageEnum {

    TO_EXECUTE("TO_EXECUTE", "待执行"),
    EXECUTING("EXECUTING", "执行中"),
    EXECUTED("EXECUTED", "执行完成"),
    NON_EXECUTE("NON_EXECUTE", "不需要执行"),

    ;

    private String key;

    private String value;

    PlanStageEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static String getValue(String key) {
        PlanStageEnum[] enums = PlanStageEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (StringUtils.equals(key, enums[i].getKey())) {
                return enums[i].getValue();
            }
        }
        return "";
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
