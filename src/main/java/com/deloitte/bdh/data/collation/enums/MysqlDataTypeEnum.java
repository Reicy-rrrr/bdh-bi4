package com.deloitte.bdh.data.collation.enums;

import com.deloitte.bdh.common.exception.BizException;

/**
 * MySQL数据类型枚举
 *
 * @author chenghzhang
 */
public enum MysqlDataTypeEnum {
    TINYINT("tinyint", DataTypeEnum.Integer, "小整数值"),
    SMALLINT("smallint", DataTypeEnum.Integer, "大整数值"),
    MEDIUMINT("mediumint", DataTypeEnum.Integer, "大整数值"),
    INT("int", DataTypeEnum.Integer, "大整数值"),
    INTEGER("integer", DataTypeEnum.Integer, "大整数值"),
    BIGINT("bigint", DataTypeEnum.Integer, "极大整数值"),
    FLOAT("float", DataTypeEnum.Float, "单精度浮点数值"),
    DOUBLE("double", DataTypeEnum.Float, "双精度浮点数值"),
    DECIMAL("decimal", DataTypeEnum.Float, "小数值"),
    DATE("date", DataTypeEnum.Date, "日期值"),
    TIME("time", DataTypeEnum.DateTime, "时间值或持续时间"),
    YEAR("year", DataTypeEnum.Date, "年份值"),
    DATETIME("datetime", DataTypeEnum.DateTime, "混合日期和时间值"),
    TIMESTAMP("timestamp", DataTypeEnum.DateTime, "混合日期和时间值，时间戳"),
    CHAR("char", DataTypeEnum.Text, "定长字符串"),
    VARCHAR("varchar", DataTypeEnum.Text, "变长字符串"),
    TINYBLOB("tinyblob", DataTypeEnum.Text, "不超过255个字符的二进制字符串"),
    TINYTEXT("tinytext", DataTypeEnum.Text, "短文本字符串"),
    BLOB("blob", DataTypeEnum.Text, "二进制形式的长文本数据"),
    TEXT("text", DataTypeEnum.Text, "长文本数据"),
    MEDIUMBLOB("mediumblob", DataTypeEnum.Text, "二进制形式的中等长度文本数据"),
    MEDIUMTEXT("mediumtext", DataTypeEnum.Text, "中等长度文本数据"),
    LONGBLOB("longblob", DataTypeEnum.Text, "二进制形式的极大文本数据"),
    LONGTEXT("longtext", DataTypeEnum.Text, "极大文本数据"),
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
