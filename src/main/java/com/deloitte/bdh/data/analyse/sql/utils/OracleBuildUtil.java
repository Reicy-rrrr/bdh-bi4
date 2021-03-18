package com.deloitte.bdh.data.analyse.sql.utils;

import com.deloitte.bdh.data.analyse.enums.AggregateTypeEnum;
import com.deloitte.bdh.data.analyse.enums.DataModelTypeEnum;
import com.deloitte.bdh.data.analyse.enums.DataUnitEnum;
import com.deloitte.bdh.data.analyse.sql.enums.OracleFormatTypeEnum;
import com.deloitte.bdh.data.collation.enums.OracleDataTypeEnum;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

import java.util.List;


public class OracleBuildUtil extends RelaBaseBuildUtil {

    public static final List<String> MENSURE_DECIMAL_TYPE = Lists.newArrayList(
            OracleDataTypeEnum.FLOAT.getType().toUpperCase(),
            OracleDataTypeEnum.DOUBLE.getType().toUpperCase(),
            OracleDataTypeEnum.DOUBLE_PRECISION.getType().toUpperCase(),
            OracleDataTypeEnum.NUMBER.getType().toUpperCase(),
            OracleDataTypeEnum.DECIMAL.getType().toUpperCase()
    );

    public static final List<String> DATE_TYPE = Lists.newArrayList(
            OracleDataTypeEnum.DATE.getType().toUpperCase(),
            OracleDataTypeEnum.TIME.getType().toUpperCase(),
            OracleDataTypeEnum.DATETIME.getType().toUpperCase(),
            OracleDataTypeEnum.TIMESTAMP_WITH_TIME_ZONE.getType().toUpperCase(),
            OracleDataTypeEnum.TIMESTAMP_WITH_LOCAL_TIME_ZONE.getType().toUpperCase(),
            OracleDataTypeEnum.TIMESTAMP.getType().toUpperCase()
    );

    public static final List<String> ESCAPE_CHARACTER = Lists.newArrayList(
            "'", "%", "\"");

    public static String select(String tableName, String field, String quota, String aggregateType,
                                String formatType, String dataType, String dataUnit, Integer precision, String alias, String defaultValue) {
        //获取表名+字段名
        String fieldExpress = selectField(tableName, field);
        //判断度量和维度
        if (DataModelTypeEnum.DL.getCode().equals(quota)) {
            fieldExpress = aggregate(fieldExpress, aggregateType);
            if (StringUtils.isNotBlank(dataUnit)) {
                fieldExpress = calWithUnit(fieldExpress, dataUnit);
            }
            //设置度量的精度
            if (StringUtils.isNotBlank(dataType) && MENSURE_DECIMAL_TYPE.contains(dataType.toUpperCase())) {
                //均值，小数点自动情况下设置保留8位
                if (null == precision && StringUtils.isNotBlank(aggregateType) && StringUtils.equals(aggregateType, AggregateTypeEnum.AVG.getKey())) {
                    precision = 8;
                }
                if (null != precision) {
                    fieldExpress = formatPrecision(fieldExpress, precision);
                }
            }

            //设置默认值
            if (StringUtils.isNotBlank(defaultValue)) {
                fieldExpress = ifNull(fieldExpress);
            }
        } else {
            //维度若是时间类型则format
            if (StringUtils.isNotBlank(formatType) && DATE_TYPE.contains(dataType.toUpperCase())) {
                fieldExpress = format(fieldExpress, formatType);
            }
        }
        return fieldExpress + " AS " + (StringUtils.isBlank(alias) ? field : alias) + " ";
    }

    public static String from(String tableName, String alias) {
        return tableName + " " + (StringUtils.isBlank(alias) ? tableName : alias) + " ";
    }

    public static String where(String tableName, String field, String quota, String formatType, String symbol, String value) {
        if (DataModelTypeEnum.DL.getCode().equals(quota)) {
            return null;
        }
        String fieldExpress = selectField(tableName, field);
        if (StringUtils.isNotBlank(formatType)) {
            fieldExpress = format(fieldExpress, formatType);
        }
        return condition(fieldExpress, symbol, value);
    }

    public static String groupBy(String tableName, String field, String quota, String formatType, String dataType, boolean needGroup) {
        if (DataModelTypeEnum.DL.getCode().equals(quota)) {
            return null;
        }
        if (!needGroup) {
            return null;
        }
        if (StringUtils.isNotBlank(formatType) && DATE_TYPE.contains(dataType.toUpperCase())) {
            return format(field, formatType);
        } else {
            return selectField(tableName, field);
        }
    }

    public static String having(String tableName, String field, String quota, String aggregateType, String symbol, String value) {
        if (DataModelTypeEnum.WD.getCode().equals(quota)) {
            return null;
        }
        return condition(aggregate(selectField(tableName, field), aggregateType), symbol, value);
    }

    public static String orderBy(String tableName, String field, String quota, String aggregateType, String formatType, String orderType) {
        if (StringUtils.isBlank(orderType)) {
            return null;
        }
        String fieldExpress = selectField(tableName, field);
        if (DataModelTypeEnum.DL.getCode().equals(quota)) {
            fieldExpress = aggregate(fieldExpress, aggregateType);
        } else {
            if (StringUtils.isNotBlank(formatType)) {
                fieldExpress = format(fieldExpress, formatType);
            }
        }
        return fieldExpress + " " + orderType;
    }

    private static String ifNull(String field) {
        return " NVL( " + field + " ,0)";
    }

    private static String formatPrecision(String field, Integer precision) {
        return " ROUND( " + field + " ," + precision + ")";
    }

    private static String format(String field, String formatType) {
        return OracleFormatTypeEnum.get(formatType).expression(field);
    }

    private static String calWithUnit(String field, String dataUnit) {
        return DataUnitEnum.values(dataUnit).expression(field);
    }

}
