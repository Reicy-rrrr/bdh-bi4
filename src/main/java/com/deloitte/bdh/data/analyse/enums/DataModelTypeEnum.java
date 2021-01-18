package com.deloitte.bdh.data.analyse.enums;

import com.deloitte.bdh.common.exception.BizException;

/**
 * Author:LIJUN
 * Date:09/11/2020
 * Description:
 */
public enum DataModelTypeEnum {

    WD("WD", "维度"),
    DL("DL", "度量"),
    ;

    private final String code;

    private final String desc;

    DataModelTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDesc(String code) {
        DataModelTypeEnum[] enums = DataModelTypeEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (enums[i].getCode().equals(code)) {
                return enums[i].getDesc();
            }
        }
        return "";
    }

    public static DataModelTypeEnum values(String code) {
        DataModelTypeEnum[] enums = DataModelTypeEnum.values();
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
