package com.deloitte.bdh.data.analyse.sql.utils;

import com.deloitte.bdh.data.analyse.enums.AggregateTypeEnum;
import com.deloitte.bdh.data.analyse.enums.DataModelTypeEnum;
import org.apache.commons.lang.StringUtils;


public class RelaBaseBuildUtil {


    public static String from(String tableName, String alias) {
        return tableName + " AS " + (StringUtils.isBlank(alias) ? tableName : alias) + " ";
    }

    public static String where(String tableName, String field, String quota, String symbol, String value) {
        if (DataModelTypeEnum.DL.getCode().equals(quota)) {
            return null;
        }
        return condition(selectField(tableName, field), symbol, value);
    }


    public static String selectField(String tableName, String field) {
        return " " + tableName + "." + field + " ";
    }

    public static String condition(String field, String symbol, String value) {
        if (StringUtils.isBlank(symbol) || StringUtils.isBlank(value)) {
            return null;
        }
        return field + " " + symbol + " " + value;
    }

    public static String aggregate(String field, String aggregateType) {
        return AggregateTypeEnum.get(aggregateType).expression(field);
    }
}
