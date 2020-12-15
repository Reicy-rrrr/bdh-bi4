package com.deloitte.bdh.data.collation.enums;

import com.deloitte.bdh.common.exception.BizException;

/**
 * MySQL数据类型枚举
 *
 * @author chenghzhang
 */
public enum MysqlDataTypeEnum {
    BIGINT("bigint", DataTypeEnum.Integer, ""),
    BINARY("binary", DataTypeEnum.Text, ""),
    BIT("bit", DataTypeEnum.Text, ""),
    BLOB("blob", DataTypeEnum.Text, ""),
    CHAR("char", DataTypeEnum.Text, ""),
    DATE("date", DataTypeEnum.Date, ""),
    DATETIME("datetime", DataTypeEnum.DateTime, ""),
    DECIMAL("decimal", DataTypeEnum.Float, ""),
    DOUBLE("double", DataTypeEnum.Float, ""),
    ENUM("enum", DataTypeEnum.Text, ""),
    FLOAT("float", DataTypeEnum.Float, ""),
    GEOMETRY("geometry", DataTypeEnum.Text, ""),
    GEOMETRYCOLLECTION("geometrycollection", DataTypeEnum.Text, ""),
    INT("int", DataTypeEnum.Integer, ""),
    INTEGER("integer", DataTypeEnum.Integer, ""),
    JSON("json", DataTypeEnum.Text, ""),
    LINESTRING("linestring", DataTypeEnum.Text, ""),
    LONGBLOB("longblob", DataTypeEnum.Text, ""),
    LONGTEXT("longtext", DataTypeEnum.Text, ""),
    MEDIUMBLOB("mediumblob", DataTypeEnum.Text, ""),
    MEDIUMINT("mediumint", DataTypeEnum.Integer, ""),
    MEDIUMTEXT("mediumtext", DataTypeEnum.Float, ""),
    MULTILINESTRING("multilinestring", DataTypeEnum.Float, ""),
    MULTIPOINT("multipoint", DataTypeEnum.Text, ""),
    MULTIPOLYGON("multipolygon", DataTypeEnum.Text, ""),
    NUMERIC("numeric", DataTypeEnum.Float, ""),
    POINT("point", DataTypeEnum.Text, ""),
    POLYGON("polygon", DataTypeEnum.Text, ""),
    REAL("real", DataTypeEnum.Integer, ""),
    SET("set", DataTypeEnum.Text, ""),
    SMALLINT("smallint", DataTypeEnum.Integer, ""),
    TEXT("text", DataTypeEnum.Text, ""),
    TIME("time", DataTypeEnum.DateTime, ""),
    TIMESTAMP("timestamp", DataTypeEnum.DateTime, ""),
    TINYBLOB("tinyblob", DataTypeEnum.Text, ""),
    TINYINT("tinyint", DataTypeEnum.Integer, ""),
    TINYTEXT("tinytext", DataTypeEnum.Text, ""),
    VARBINARY("varbinary", DataTypeEnum.Text, ""),
    VARCHAR("varchar", DataTypeEnum.Text, ""),
    YEAR("year", DataTypeEnum.Integer, ""),
    ;

    private String type;

    private DataTypeEnum value;

    private String desc;

    MysqlDataTypeEnum(String type, DataTypeEnum value, String desc) {
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
        MysqlDataTypeEnum[] enums = MysqlDataTypeEnum.values();
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
    public static MysqlDataTypeEnum values(String type) {
        MysqlDataTypeEnum[] enums = MysqlDataTypeEnum.values();
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
