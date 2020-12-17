package com.deloitte.bdh.data.analyse.sql.enums;


public enum SqlserverFormatTypeEnum {
    YEAR("YEAR", "年"),

    YEAR_QUARTERLY("YEAR_QUARTERLY", "年-季度") {
        @Override
        public String expression(String str) {
            return " CONCAT(YEAR(str),'-',DATEPART( Q , str))".replaceAll("str", str);
        }
    },

    YEAR_MONTH("YEAR_MONTH", "年-月") {
        @Override
        public String expression(String str) {
            return " format( str ,'yyyy-MM')".replaceAll("str", str);
        }
    },

    YEAR_WEEK("YEAR_WEEK", "年-周(跨年)") {
        @Override
        public String expression(String str) {
            return " CONCAT(YEAR(str),'-',DATEPART( WW , str))".replaceAll("str", str);
        }
    },

    YEAR_WEEK_EVE("YEAR_WEEK_EVE", "年-周(不跨年)") {
        @Override
        public String expression(String str) {
            return " CONCAT(YEAR(str),'-',DATEPART( WW , str))".replaceAll("str", str);
        }
    },

    YEAR_MONTH_DAY("YEAR_MONTH_DAY", "年-月-日") {
        @Override
        public String expression(String str) {
            return " format( str ,'yyyy-MM-dd')".replaceAll("str", str);
        }
    },

    QUARTER("QUARTER", "季度") {
        @Override
        public String expression(String str) {
            return " DATEPART(Q , str)".replaceAll("str", str);
        }
    },

    MONTH("MONTH", "月") ,

    WEEK("WEEK", "周") {
        @Override
        public String expression(String str) {
            return " DATEPART( WW , str)".replaceAll("str", str);
        }
    },

    DAYOFWEEK("DAYOFWEEK", "星期") {
        @Override
        public String expression(String str) {
            return " DATEPART( dw , str)".replaceAll("str", str);
        }
    },

    DAY("DAY", "日") ,

    HOUR("HOUR", "小时") {
        @Override
        public String expression(String str) {
            return " DATEPART( HH , str)".replaceAll("str", str);
        }
    },

    ;

    private final String key;

    private final String value;

    SqlserverFormatTypeEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static SqlserverFormatTypeEnum get(String key) {
        SqlserverFormatTypeEnum[] enums = SqlserverFormatTypeEnum.values();
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
