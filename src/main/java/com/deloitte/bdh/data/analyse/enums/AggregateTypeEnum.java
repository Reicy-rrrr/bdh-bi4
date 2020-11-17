package com.deloitte.bdh.data.analyse.enums;



public enum AggregateTypeEnum {
    SUM("SUM", "求和"),
    AVG("AVG", "均值"),
    MAX("MAX", "最大"),
    MIN("MIN", "最小"),
    COUNT("COUNT", "计数"),
    COUNT_DISTINCT("COUNT_DISTINCT", "去重复") {
        @Override
        public String expression(String str) {
            return "COUNT(DISTINCT " + str + ")";
        }
    },
    ;

    private final String key;

    private final String value;

    AggregateTypeEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static AggregateTypeEnum get(String key) {
        AggregateTypeEnum[] enums = AggregateTypeEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (enums[i].getKey().equals(key)) {
                return enums[i];
            }
        }
        //默认值SUM
        return SUM;
    }

    public String expression(String str) {
        return this.getKey() + "(" + str + ")";
    }

}
