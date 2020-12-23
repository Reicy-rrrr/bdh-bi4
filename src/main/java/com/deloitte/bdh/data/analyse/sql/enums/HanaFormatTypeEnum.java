package com.deloitte.bdh.data.analyse.sql.enums;


import com.deloitte.bdh.data.analyse.enums.FormatTypeEnum;

public enum HanaFormatTypeEnum {
    YEAR(FormatTypeEnum.YEAR.getKey(), "年"),
    YEAR_QUARTERLY(FormatTypeEnum.YEAR_QUARTERLY.getKey(), "年-季度") {
        @Override
        public String expression(String str) {
            return " CONCAT(YEAR(str), '-', QUARTER(str))".replaceAll("str", str);
        }
    },
    YEAR_MONTH(FormatTypeEnum.YEAR_MONTH.getKey(), "年-月") {
        @Override
        public String expression(String str) {
            return " to_char( str ,'yyyy-MM')".replaceAll("str", str);
        }
    },
    YEAR_WEEK(FormatTypeEnum.YEAR_WEEK.getKey(), "年-周(跨年)") {
        @Override
        public String expression(String str) {
            return " CONCAT(CONCAT(year(str), '-') ,WEEK(str))".replaceAll("str", str);
        }
    },

    YEAR_WEEK_EVE(FormatTypeEnum.YEAR_WEEK_EVE.getKey(), "年-周(不跨年)") {
        @Override
        public String expression(String str) {
            return " CONCAT(CONCAT(year(str), '-') ,WEEK(str))".replaceAll("str", str);
        }
    },

    YEAR_MONTH_DAY(FormatTypeEnum.YEAR_MONTH_DAY.getKey(), "年-月-日") {
        @Override
        public String expression(String str) {
            return " to_char( str ,'yyyy-MM-dd')".replaceAll("str", str);
        }
    },

    QUARTER(FormatTypeEnum.QUARTER.getKey(), "季度") {
        @Override
        public String expression(String str) {
            return " RIGHT(quarter(str),1)".replaceAll("str", str);
        }
    },

    MONTH(FormatTypeEnum.MONTH.getKey(), "月"),

    WEEK(FormatTypeEnum.WEEK.getKey(), "周"),

    DAYOFWEEK(FormatTypeEnum.DAYOFWEEK.getKey(), "星期"),

    DAY(FormatTypeEnum.DAY.getKey(), "日") {
        @Override
        public String expression(String str) {
            return " to_char( str ,'dd')".replaceAll("str", str);
        }
    },

    HOUR(FormatTypeEnum.HOUR.getKey(), "小时"),
    ;

    private final String key;

    private final String value;

    HanaFormatTypeEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static HanaFormatTypeEnum get(String key) {
        HanaFormatTypeEnum[] enums = HanaFormatTypeEnum.values();
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
