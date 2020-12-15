package com.deloitte.bdh.data.collation.enums;

import com.deloitte.bdh.common.exception.BizException;

/**
 * 数据类型枚举
 *
 * @author chenghzhang
 */
public enum DataTypeEnum {

    Text("Text", "varchar", "文本"),
    Integer("Integer", "bigint", "整数"),
    Float("Float", "decimal", "浮点数"),
    Date("Date", "date", "日期"),
    DateTime("DateTime", "datetime", "日期时间"),
    /** 其他类型不支持转换：blob、clob等 */
    Other("Other", "varchar", "其他类型"),
    ;

    private String type;

    private String value;

    private String desc;

    DataTypeEnum(String type, String value, String desc) {
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
        DataTypeEnum[] enums = DataTypeEnum.values();
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
    public static DataTypeEnum values(String type) {
        DataTypeEnum[] enums = DataTypeEnum.values();
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

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
