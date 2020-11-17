package com.deloitte.bdh.data.analyse.enums;


public enum FormatTypeEnum {
    YEAR("YEAR", "年"),
    YEAR_QUARTERLY("YEAR_QUARTERLY", "年-季度") {
        @Override
        public String expression(String str) {
            return " CONCAT(YEAR(str), '-', QUARTER(str))".replaceAll("str", str);
        }
    },
    YEAR_MONTH("YEAR_MONTH", "年-月") {
        @Override
        public String expression(String str) {
            return " DATE_FORMAT(CONCAT(DATE(str), ' ', FLOOR(MONTH(str) / 1) * 1), '%Y-%m')".replaceAll("str", str);
        }
    },
    YEAR_WEEK("YEAR_WEEK", "年-周(跨年)") {
        @Override
        public String expression(String str) {
            return " DATE_FORMAT(str, '%X-%V')".replaceAll("str", str);
        }
    },
    YEAR_WEEK_EVE("YEAR_WEEK_EVE", "年-周(不跨年)") {
        @Override
        public String expression(String str) {
            return " DATE_FORMAT(str, '%Y-%u')".replaceAll("str", str);
        }
    },
    YEAR_MONTH_DAY("YEAR_MONTH_DAY", "年-月-日") {
        @Override
        public String expression(String str) {
            return " DATE_FORMAT(str, '%Y-%m-%d')".replaceAll("str", str);
        }
    },
    QUARTER("QUARTER", "季度"),
    MONTH("MONTH", "月"),
    WEEK("WEEK", "周"),
    DAYOFWEEK("DAYOFWEEK", "星期"),
    DAY("DAY", "日"),
    HOUR("HOUR", "小时"),
    ;

    private final String key;

    private final String value;

    FormatTypeEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static FormatTypeEnum get(String key) {
        FormatTypeEnum[] enums = FormatTypeEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (enums[i].getKey().equals(key)) {
                return enums[i];
            }
        }
        throw new RuntimeException("暂不支持的类型！");
    }

    public String expression(String str) {
        return this.getKey() + "(" + str + ")";
    }

}
