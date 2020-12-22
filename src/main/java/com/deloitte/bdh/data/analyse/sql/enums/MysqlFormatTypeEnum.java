package com.deloitte.bdh.data.analyse.sql.enums;


import com.deloitte.bdh.data.analyse.enums.FormatTypeEnum;

public enum MysqlFormatTypeEnum {
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
            return " DATE_FORMAT(CONCAT(DATE(str), ' ', FLOOR(MONTH(str) / 1) * 1), '%Y-%m')".replaceAll("str", str);
        }
    },
    YEAR_WEEK(FormatTypeEnum.YEAR_WEEK.getKey(), "年-周(跨年)") {
        @Override
        public String expression(String str) {
            return " DATE_FORMAT(str, '%X-%V')".replaceAll("str", str);
        }
    },
    YEAR_WEEK_EVE(FormatTypeEnum.YEAR_WEEK_EVE.getKey(), "年-周(不跨年)") {
        @Override
        public String expression(String str) {
            return " DATE_FORMAT(str, '%Y-%u')".replaceAll("str", str);
        }
    },
    YEAR_MONTH_DAY(FormatTypeEnum.YEAR_MONTH_DAY.getKey(), "年-月-日") {
        @Override
        public String expression(String str) {
            return " DATE_FORMAT(str, '%Y-%m-%d')".replaceAll("str", str);
        }
    },
    QUARTER(FormatTypeEnum.QUARTER.getKey(), "季度"),
    MONTH(FormatTypeEnum.MONTH.getKey(), "月"),
    WEEK(FormatTypeEnum.WEEK.getKey(), "周") {
        @Override
        public String expression(String str) {
            return " WEEKOFYEAR(" + str + ")";
        }
    },
    DAYOFWEEK(FormatTypeEnum.DAYOFWEEK.getKey(), "星期"),
    DAY(FormatTypeEnum.DAY.getKey(), "日"),
    HOUR("HOUR", "小时"),
    ;

    private final String key;

    private final String value;

    MysqlFormatTypeEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static MysqlFormatTypeEnum get(String key) {
        MysqlFormatTypeEnum[] enums = MysqlFormatTypeEnum.values();
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
