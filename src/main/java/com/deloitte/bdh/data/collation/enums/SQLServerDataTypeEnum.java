package com.deloitte.bdh.data.collation.enums;

import com.deloitte.bdh.common.exception.BizException;

/**
 * SQLServer数据类型枚举
 *
 * @author chenghzhang
 */
public enum SQLServerDataTypeEnum {
    BIGINT("bigint", DataTypeEnum.Integer, ""),
    TIMESTAMP("timestamp", DataTypeEnum.Text, ""),
    BINARY("binary", DataTypeEnum.Text, ""),
    BIT("bit", DataTypeEnum.Text, ""),
    CHAR("char", DataTypeEnum.Text, ""),
    DECIMAL("decimal", DataTypeEnum.Float, ""),
    MONEY("money", DataTypeEnum.Float, ""),
    SMALL_MONEY("smallmoney", DataTypeEnum.Float, ""),
    FLOAT("float", DataTypeEnum.Float, ""),
    INT("int", DataTypeEnum.Integer, ""),
    IMAGE("image", DataTypeEnum.Text, ""),
    VARBINARY_MAX("varbinary(max)", DataTypeEnum.Text, ""),
    VARCHAR_MAX("varchar(max)", DataTypeEnum.Text, ""),
    TEXT("text", DataTypeEnum.Text, ""),
    NCHAR("nchar", DataTypeEnum.Text, ""),
    NVARCHAR("nvarchar", DataTypeEnum.Text, ""),
    NVARCHAR_MAX("nvarchar(max)", DataTypeEnum.Text, ""),
    NTEXT("ntext", DataTypeEnum.Text, ""),
    NUMERIC("numeric", DataTypeEnum.Float, ""),
    REAL("real", DataTypeEnum.Float, ""),
    SMALLINT("smallint", DataTypeEnum.Integer, ""),
    DATE("date", DataTypeEnum.Date, ""),
    DATETIME("datetime", DataTypeEnum.DateTime, ""),
    SMALL_DATETIME("smalldatetime", DataTypeEnum.DateTime, ""),
    VARBINARY("varbinary", DataTypeEnum.Text, ""),
    UDT("udt", DataTypeEnum.Text, ""),
    VARCHAR("varchar", DataTypeEnum.Text, ""),
    TINYINT("tinyint", DataTypeEnum.Integer, ""),
    UNIQUE_IDENTIFIER("uniqueidentifier", DataTypeEnum.Integer, ""),
    XML("xml", DataTypeEnum.Integer, ""),
    ;

    private String type;

    private DataTypeEnum value;

    private String desc;

    SQLServerDataTypeEnum(String type, DataTypeEnum value, String desc) {
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
        SQLServerDataTypeEnum[] enums = SQLServerDataTypeEnum.values();
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
    public static SQLServerDataTypeEnum values(String type) {
        SQLServerDataTypeEnum[] enums = SQLServerDataTypeEnum.values();
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
