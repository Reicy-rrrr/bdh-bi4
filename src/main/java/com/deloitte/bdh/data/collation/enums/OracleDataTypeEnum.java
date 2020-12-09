package com.deloitte.bdh.data.collation.enums;

import com.deloitte.bdh.common.exception.BizException;

/**
 * MySQL数据类型枚举
 *
 * @author chenghzhang
 */
public enum OracleDataTypeEnum {
    CHAR("CHAR", DataTypeEnum.Text, "固定长度字符串"),
    VARCHAR2("VARCHAR2", DataTypeEnum.Text, "可变长度的字符串"),
    NCHAR("NCHAR", DataTypeEnum.Text, "根据字符集而定的固定长度字符串"),
    NVARCHAR2("NVARCHAR2", DataTypeEnum.Text, "根据字符集而定的可变长度字符串"),
    DATE("DATE", DataTypeEnum.Date, "日期（日-月-年）"),
    TIMESTAMP("TIMESTAMP", DataTypeEnum.DateTime, "日期（日-月-年）"),
    LONG("LONG", DataTypeEnum.Text, "超长字符串"),
    RAW("RAW", DataTypeEnum.Text, "固定长度的二进制数据"),
    LONG_RAW("LONG RAW", DataTypeEnum.Text, "可变长度的二进制数据"),
    BLOB("BLOB", DataTypeEnum.Text, "二进制数据"),
    CLOB("CLOB", DataTypeEnum.Text, "字符数据"),
    NCLOB("NCLOB", DataTypeEnum.Text, "根据字符集而定的字符数据"),
    BFILE("BFILE", DataTypeEnum.Text, "存放在数据库外的二进制数据"),
    ROWID("ROWID", DataTypeEnum.Integer, "数据表中记录的唯一行号"),
    NROWID("NROWID", DataTypeEnum.Integer, "二进制数据表中记录的唯一行号"),
    NUMBER("NUMBER", DataTypeEnum.Float, "数字类型"),
    DECIMAL("DECIMAL", DataTypeEnum.Float, "数字类型"),
    INTEGER("INTEGER", DataTypeEnum.Integer, "整数类型"),
    FLOAT("FLOAT", DataTypeEnum.Float, "浮点数类型"),
    REAL("REAL", DataTypeEnum.Float, "实数类型"),
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
