package com.deloitte.bdh.data.collation.enums;

import com.deloitte.bdh.common.exception.BizException;

/**
 * 数据类型枚举
 *
 * @author chenghzhang
 */
public enum DataTypeEnum {

    String("String", "文本"),
    Integer("Integer", "整数"),
    Float("Float", "浮点数"),
    Date("Date", "日期"),
    ;

    private String value;

    private String desc;

    DataTypeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    /**
     * 根据value获取描述
     *
     * @param value 文件类型
     * @return String
     */
    public static String getDesc(String value) {
        DataTypeEnum[] enums = DataTypeEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (enums[i].getValue().equals(value)) {
                return enums[i].getDesc();
            }
        }
        return "";
    }

    /**
     * 根据类型获取枚举类型
     *
     * @param value
     * @return
     */
    public static DataTypeEnum values(String value) {
        DataTypeEnum[] enums = DataTypeEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (enums[i].getValue().equals(value)) {
                return enums[i];
            }
        }
        throw new BizException("暂不支持的数据类型！");
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
