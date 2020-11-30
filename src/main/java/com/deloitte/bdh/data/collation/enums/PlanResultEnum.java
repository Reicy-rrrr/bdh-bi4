package com.deloitte.bdh.data.collation.enums;


import org.apache.commons.lang3.StringUtils;

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

    public static String getValue(String key) {
        if (null != key && !"".equals(key)) {
            PlanResultEnum[] enums = PlanResultEnum.values();
            for (int i = 0; i < enums.length; i++) {
                if (StringUtils.equals(key, enums[i].getKey())) {
                    return enums[i].getValue();
                }
            }
        }
        return "未执行";
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
