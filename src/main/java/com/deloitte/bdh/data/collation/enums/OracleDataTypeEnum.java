package com.deloitte.bdh.data.collation.enums;

import com.deloitte.bdh.common.exception.BizException;

/**
 * Oracle数据类型枚举
 *
 * @author chenghzhang
 */
public enum OracleDataTypeEnum {
    NUMBER("NUMBER", DataTypeEnum.Float, ""),
    SMALLINT("SMALLINT", DataTypeEnum.Integer, ""),
    INT("INT", DataTypeEnum.Integer, ""),
    INTEGER("INTEGER", DataTypeEnum.Integer, ""),
    CHAR("CHAR", DataTypeEnum.Text, ""),
    CHARACTER("CHARACTER", DataTypeEnum.Text, ""),
    NCHAR("NCHAR", DataTypeEnum.Text, ""),
    VARCHAR("VARCHAR", DataTypeEnum.Text, ""),
    VARCHAR2("VARCHAR2", DataTypeEnum.Text, ""),
    NVARCHAR2("NVARCHAR2", DataTypeEnum.Text, ""),
    LONG("LONG", DataTypeEnum.Text, ""),
    CLOB("CLOB", DataTypeEnum.Text, ""),
    NCLOB("NCLOB", DataTypeEnum.Text, ""),
    DEC("DEC", DataTypeEnum.Float, ""),
    DECIMAL("DECIMAL", DataTypeEnum.Float, ""),
    DOUBLE_PRECISION("DOUBLE PRECISION", DataTypeEnum.Float, ""),
    BLOB("BLOB", DataTypeEnum.Text, ""),
    BFILE("BFILE", DataTypeEnum.Text, ""),
    RAW("RAW", DataTypeEnum.Text, ""),
    LONG_VARCHAR("LONG VARCHAR", DataTypeEnum.Text, ""),
    DATE("DATE", DataTypeEnum.Date, ""),
    TIME("TIME", DataTypeEnum.DateTime, ""),
    DATETIME("DATETIME", DataTypeEnum.DateTime, ""),
    TIMESTAMP("TIMESTAMP", DataTypeEnum.DateTime, ""),
    TIMESTAMP_WITH_TIME_ZONE("TIMESTAMP WITH TIME ZONE", DataTypeEnum.DateTime, ""),
    TIMESTAMP_WITH_LOCAL_TIME_ZONE("TIMESTAMP WITH LOCAL TIME ZONE", DataTypeEnum.DateTime, ""),
    INTERVAL("INTERVAL", DataTypeEnum.Date, ""),
    INTERVAL_YEAR_TO_MOTH("INTERVAL YEAR TO MOTH", DataTypeEnum.Date, ""),
    INTERVAL_DAY_TO_SECOND("INTERVAL DAY TO SECOND", DataTypeEnum.Date, ""),
    YEAR("YEAR", DataTypeEnum.Date, ""),
    FLOAT("FLOAT", DataTypeEnum.Float, ""),
    BINARY_FLOAT("BINARY_FLOAT", DataTypeEnum.Float, ""),
    DOUBLE("DOUBLE", DataTypeEnum.Float, ""),
    BINARY_DOUBLE("BINARY_DOUBLE", DataTypeEnum.Float, ""),
    BOOL("BOOL", DataTypeEnum.Text, ""),
    BOOLEAN("BOOLEAN", DataTypeEnum.Text, ""),
    NUMBER_INTEGER("NUMBER_INTEGER", DataTypeEnum.Integer, ""),
    NUMBER_LONG("NUMBER_LONG", DataTypeEnum.Integer, ""),
    ;

    private String type;

    private DataTypeEnum value;

    private String desc;

    OracleDataTypeEnum(String type, DataTypeEnum value, String desc) {
        this.type = type;
        this.value = value;
        this.desc = desc;
    }

    /**
     * 根据value获取描述
     *
     * @param type 文件类型
     * @return String
     */
    public static String getDesc(String type) {
        OracleDataTypeEnum[] enums = OracleDataTypeEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (enums[i].getType().equals(type)) {
                return enums[i].getDesc();
            }
        }
        return "";
    }

    /**
     * 根据类型获取枚举类型
     *
     * @param type
     * @return
     */
    public static OracleDataTypeEnum values(String type) {
        OracleDataTypeEnum[] enums = OracleDataTypeEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (enums[i].getType().equals(type)) {
                return enums[i];
            }
        }
        throw new BizException("暂不支持的数据类型！");
    }

    public String getType() {
        return type;
    }

    public DataTypeEnum getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
