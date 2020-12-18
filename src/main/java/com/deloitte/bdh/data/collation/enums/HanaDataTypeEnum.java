package com.deloitte.bdh.data.collation.enums;

import com.deloitte.bdh.common.exception.BizException;

/**
 * Hana数据类型枚举
 *
 * @author chenghzhang
 */
public enum HanaDataTypeEnum {
    ALPHANUM("ALPHANUM", DataTypeEnum.Text, ""),
    BIGINT("BIGINT", DataTypeEnum.Integer, ""),
    BINARY("BINARY", DataTypeEnum.Text, ""),
    BINTEXT("BINTEXT", DataTypeEnum.Text, ""),
    BLOB("BLOB", DataTypeEnum.Text, ""),
    BOOLEAN("BOOLEAN", DataTypeEnum.Text, ""),
    CHAR("CHAR", DataTypeEnum.Text, ""),
    CLOB("CLOB", DataTypeEnum.Text, ""),
    DATE("DATE", DataTypeEnum.Date, ""),
    DECIMAL("DECIMAL", DataTypeEnum.Float, ""),
    DOUBLE("DOUBLE", DataTypeEnum.Float, ""),
    INTEGER("INTEGER", DataTypeEnum.Integer, ""),
    NCLOB("NCLOB", DataTypeEnum.Text, ""),
    NVARCHAR("NVARCHAR", DataTypeEnum.Text, ""),
    REAL("REAL", DataTypeEnum.Float, ""),
    SECONDDATE("SECONDDATE", DataTypeEnum.Date, ""),
    SHORTTEXT("SHORTTEXT", DataTypeEnum.Text, ""),
    SMALLDECIMAL("SMALLDECIMAL", DataTypeEnum.Float, ""),
    SMALLINT("SMALLINT", DataTypeEnum.Integer, ""),
    ST_GEOMETRY("ST_GEOMETRY", DataTypeEnum.Text, ""),
    ST_POINT("ST_POINT", DataTypeEnum.Text, ""),
    TEXT("TEXT", DataTypeEnum.Text, ""),
    TIME("TIME", DataTypeEnum.DateTime, ""),
    TIMESTAMP("TIMESTAMP", DataTypeEnum.DateTime, ""),
    TINYINT("TINYINT", DataTypeEnum.Integer, ""),
    VARBINARY("VARBINARY", DataTypeEnum.Text, ""),
    VARCHAR("VARCHAR", DataTypeEnum.Text, ""),
    ;

    private String type;

    private DataTypeEnum value;

    private String desc;

    HanaDataTypeEnum(String type, DataTypeEnum value, String desc) {
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
        HanaDataTypeEnum[] enums = HanaDataTypeEnum.values();
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
    public static HanaDataTypeEnum get(String type) {
        HanaDataTypeEnum[] enums = HanaDataTypeEnum.values();
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
