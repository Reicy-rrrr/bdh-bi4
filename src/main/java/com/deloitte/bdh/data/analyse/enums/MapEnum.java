package com.deloitte.bdh.data.analyse.enums;

import com.deloitte.bdh.common.exception.BizException;

public enum MapEnum {

    LONGITUDE("LONGITUDE", "经度"),
    LATITUDE("LATITUDE", "纬度"),
    ;

    private String code;

    private String desc;

    MapEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDesc(String code) {
        MapEnum[] enums = MapEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (enums[i].getCode().equals(code)) {
                return enums[i].getDesc();
            }
        }
        return "";
    }

    public static MapEnum values(String code) {
        MapEnum[] enums = MapEnum.values();
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
