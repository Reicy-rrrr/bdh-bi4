package com.deloitte.bdh.data.analyse.enums;


import com.deloitte.bdh.common.exception.BizException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public enum WildcardEnum {
    EQ("EQ", "=", "精确匹配"),
    GT("GT", ">", "大于"),
    GTE("GT", ">=", "大于等于"),
    LT("LT", "<", "小于"),
    LTE("LTE", "<=", "小于等于"),
    IN("IN", "IN", "包含"){
        @Override
        public String expression(List<String> str) {
            return "(" + str.stream().map(s -> "'" + s + "'").collect(Collectors.joining(", ")) + ")";
        }
    },
    NOT_IN("NOT_IN", "NOT IN", "不包含"){
        @Override
        public String expression(List<String> str) {
            return "(" + str.stream().map(s -> "'" + s + "'").collect(Collectors.joining(", ")) + ")";
        }
    },
    LIKE("LIKE", "LIKE", "模糊匹配"){
        @Override
        public String expression(List<String> str) {
            return "'" + "%" + str.get(0) + "%" + "'";
        }
    },
    NOT_LIKE("NOT_LIKE", "NOT LIKE", "模糊匹配"){
        @Override
        public String expression(List<String> str) {
            return "'" + "%" + str.get(0) + "%" + "'";
        }
    },
    LIKE_PRE("LIKE_PRE", "LIKE", "开头为"){
        @Override
        public String expression(List<String> str) {
            return "'" + str.get(0) + "%" + "'";
        }
    },
    LIKE_END("LIKE_END", "LIKE", "结尾为"){
        @Override
        public String expression(List<String> str) {
            return "'" + "%" + str.get(0) + "'";
        }
    },
    IS("IS",  "IS", ""){
        @Override
        public String expression(List<String> str) {
            return "NULL";
        }
    },
    IS_NOT("IS_NOT", "IS NOT", ""){
        @Override
        public String expression(List<String> str) {
            return "NULL";
        }
    }
    ;

    private final String key;

    private final String code;

    private final String value;

    WildcardEnum(String key, String code, String value) {
        this.key = key;
        this.code = code;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }

    public static WildcardEnum get(String key) {
        WildcardEnum[] enums = WildcardEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (enums[i].getKey().equals(key)) {
                return enums[i];
            }
        }
        throw new BizException("非法通配符");
    }

    public String expression(List<String> valueList) {
        return "'" + valueList.get(0) + "'";
    }

}
