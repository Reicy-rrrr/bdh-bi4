package com.deloitte.bdh.data.analyse.enums;

import com.deloitte.bdh.common.exception.BizException;


public enum DataUnitEnum {

    NONE("", "自动"),
    THOUSAND("THOUSAND", "千"),
    TENTHOUSAND("TENTHOUSAND", "万"),
    MILLION("MILLION", "百万"),
    TENMILLION("TENMILLION", "千万"),
    BILLION("BILLION", "亿");

    private String code;

    private String desc;

    DataUnitEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDesc(String code) {
        DataUnitEnum[] enums = DataUnitEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (enums[i].getCode().equals(code)) {
                return enums[i].getDesc();
            }
        }
        return "";
    }

    public static DataUnitEnum values(String code) {
        DataUnitEnum[] enums = DataUnitEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (enums[i].getCode().equals(code)) {
                return enums[i];
            }
        }
        throw new BizException("暂不支持的认证类型！");
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
