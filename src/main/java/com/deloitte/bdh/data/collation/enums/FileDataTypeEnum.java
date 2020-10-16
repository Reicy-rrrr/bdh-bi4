package com.deloitte.bdh.data.collation.enums;

import com.deloitte.bdh.common.exception.BizException;

/**
 * 上传文件中数据类型枚举
 *
 * @author chenghzhang
 */
public enum FileDataTypeEnum {

    Integer("Integer", "正数"),
    Float("Float", "浮点数"),
    String("String", "文本"),
    Date("Date", "时间"),
    ;

    private String value;

    private String desc;

    FileDataTypeEnum(String value, String desc) {
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
        FileDataTypeEnum[] enums = FileDataTypeEnum.values();
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
    public static FileDataTypeEnum values(String value) {
        FileDataTypeEnum[] enums = FileDataTypeEnum.values();
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
