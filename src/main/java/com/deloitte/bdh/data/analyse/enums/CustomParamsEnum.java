package com.deloitte.bdh.data.analyse.enums;

import com.deloitte.bdh.common.exception.BizException;

/**
 * Author:LIJUN
 * Date:09/11/2020
 * Description:
 */
public enum CustomParamsEnum {

    TABLE_AGGREGATE("tableAggregate", "是否聚合"),
    ;

    private final String code;

    private final String desc;

    CustomParamsEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDesc(String code) {
        CustomParamsEnum[] enums = CustomParamsEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (enums[i].getCode().equals(code)) {
                return enums[i].getDesc();
            }
        }
        return "";
    }

    public static CustomParamsEnum values(String code) {
        CustomParamsEnum[] enums = CustomParamsEnum.values();
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
